package ru.practicum.mainservice.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.event.dto.UpdateEventRequest;
import ru.practicum.mainservice.event.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.SortType;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.model.StateAdmin;
import ru.practicum.mainservice.event.model.StateUser;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.exception.BadRequestException;
import ru.practicum.mainservice.exception.exception.ConflictException;
import ru.practicum.mainservice.exception.exception.ConstraintUpdatingException;
import ru.practicum.mainservice.exception.exception.NotFoundException;
import ru.practicum.mainservice.exception.exception.ViolationOfEditingRulesException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.model.RequestStatus;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.Util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static ru.practicum.mainservice.event.specification.EventSpecification.getAdminFilters;
import static ru.practicum.mainservice.event.specification.EventSpecification.getPublicFilters;
import static ru.practicum.mainservice.util.Util.DATE_TIME_FORMATTER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    private String serviceId;

    @Override
    @Transactional
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         SortType sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeEnd cannot be before rangeStart");
        }

        Specification<Event> spec = getPublicFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        Sort sorting = Sort.by("eventDate");

        if (sort != null) {
            if (sort == SortType.EVENT_DATE) {
                sorting = Sort.by("eventDate");
            } else if (sort == SortType.VIEWS) {
                sorting = Sort.by("views");
            }
        }

        Pageable pageable = PageRequest.of(from / size, size, sorting);


        Page<Event> events = eventRepository.findAll(spec, pageable);
        sendStatisticalData(request);

        List<Event> eventList = events.getContent().stream()
                .map(event -> {
                    Long views = getUniqueViews(event, request.getRequestURI()) + 1;
                    event.setViews(views);
                    return event;
                })
                .toList();


        eventRepository.saveAll(eventList);

        return events.getContent().stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.eventNotFound(eventId)));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(Util.eventNotFound(eventId));
        }

        Long views = getUniqueViews(event, request.getRequestURI());
        views++;
        event.setViews(views);

        event = eventRepository.save(event);

        EventFullDto fullDto = eventMapper.toEventFullDto(event);

        sendStatisticalData(request);
        return fullDto;
    }

    @Override
    public List<EventFullDto> getFullEvents(List<Long> users,
                                            List<State> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Integer from,
                                            Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> spec = getAdminFilters(users, states, categories, rangeStart, rangeEnd);


        Page<Event> events = eventRepository.findAll(spec, pageable);

        return events.getContent().stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.eventNotFound(eventId)));

        StringBuilder updatedFieldsLog = new StringBuilder();

        updateEventFields(event, updateEventAdminRequest, updatedFieldsLog);
        if (updateEventAdminRequest.getStateAction() != null) {
            handleStateAction(updateEventAdminRequest, event);
            updatedFieldsLog.append("StateAction|");
        }

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }


    private void handleStateAction(UpdateEventAdminRequest updateEventAdminRequest, Event event) {
        StateAdmin stateAction = updateEventAdminRequest.getStateAction();

        if (stateAction == StateAdmin.PUBLISH_EVENT) {
            handlePublishEvent(event);
        } else if (stateAction == StateAdmin.REJECT_EVENT) {
            handleRejectEvent(event);
        }
    }

    private void handlePublishEvent(Event event) {
        if (event.getState() == State.PENDING) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else {
            throw new ConstraintUpdatingException("Event can only be published if it is in a pending state.");
        }
    }

    private void handleRejectEvent(Event event) {
        if (event.getState() == State.PENDING) {
            event.setState(State.CANCELED);
        } else {
            throw new ConstraintUpdatingException("Event can only be rejected if it has not yet been published.");
        }
    }


    @Override
    public List<EventShortDto> getEventsByCurrentUser(Long userId, int from, int size) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> page = eventRepository.findAllByInitiator(user, pageable);

        if (!page.hasContent()) {
            return List.of();
        }

        return page.getContent().stream()
                .map(eventMapper::toShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d  not found", newEventDto.getCategory())));

        LocalDateTime minDateConstraint = LocalDateTime.now().plusHours(2);

        if (newEventDto.getEventDate().isBefore(minDateConstraint)) {
            throw new BadRequestException(String.format("Field: eventDate. " +
                    "Error: Date cant be earlier than %s. " +
                    "Value: %s", minDateConstraint, newEventDto.getEventDate()));
        }

        Event event = eventMapper.fromNewEventDto(newEventDto, category, user);

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getFullEventByIdForCurrentUser(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(Util.eventNotFound(eventId)));

        return eventMapper.toEventFullDto(event);
    }


    @Override
    @Transactional
    public EventFullDto updateByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        User user = getUserById(userId);
        Event event = getEventByIdAndInitiator(eventId, user);

        if (event.getState().equals(State.PUBLISHED)) {
            throw new ViolationOfEditingRulesException("Only pending or canceled events can be changed");
        }

        StringBuilder updatedFieldsLog = new StringBuilder();

        updateEvent(updateEventUserRequest, event, updatedFieldsLog);

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d not found", userId)));
    }

    private Event getEventByIdAndInitiator(Long eventId, User user) {
        return eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));
    }

    private void updateEvent(UpdateEventUserRequest updateEventUserRequest,
                             Event event,
                             StringBuilder updatedFieldsLog) {
        updateEventFields(event, updateEventUserRequest, updatedFieldsLog);
        if (updateEventUserRequest.getStateAction() != null) {
            handleStateAction(updateEventUserRequest, event);
            updatedFieldsLog.append("StateAction|");
        }

    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (!isStartDateValid(LocalDateTime.now(), eventDate, 2)) {
            throw new BadRequestException("The date and time for which the event is scheduled cannot be earlier" +
                    " than two hours from the current moment.");
        }
    }

    private void handleStateAction(UpdateEventUserRequest updateEventUserRequest, Event event) {
        if (updateEventUserRequest.getStateAction().equals(StateUser.SEND_TO_REVIEW)) {
            event.setState(State.PENDING);
        }
        if (updateEventUserRequest.getStateAction().equals(StateUser.CANCEL_REVIEW)
                && event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
        }
    }


    @Override
    public List<ParticipationRequestDto> getRequestsByCurrentUser(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Event event = eventRepository.findEventByIdAndInitiator(eventId, user)
                .orElseThrow(() -> new NotFoundException(Util.eventNotFound(eventId)));

        Collection<ParticipationRequest> requests = requestRepository.findAllByEvent(event);
        if (requests.isEmpty()) {
            return List.of();
        }

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Util.eventNotFound(eventId)));

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(requestRepository.findAllByEvent(event).stream().map(requestMapper::toParticipationRequestDto).toList())
                    .rejectedRequests(List.of())
                    .build();
        }

        Collection<ParticipationRequest> pendingRequests = requestRepository.findAllByEventAndStatus(event, RequestStatus.PENDING);

        if (pendingRequests.isEmpty()) {
            throw new ViolationOfEditingRulesException("Request must have status PENDING");
        }

        long confirmedRequestsCount = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);

        if (statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED) && confirmedRequestsCount >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }

        for (ParticipationRequest request : pendingRequests) {
            if (confirmedRequestsCount < event.getParticipantLimit()) {
                request.setStatus(statusUpdateRequest.getStatus());
                requestRepository.save(request);

                if (statusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                    confirmedRequestsCount++;
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
            }
        }

        event.setConfirmedRequests(confirmedRequestsCount);
        eventRepository.save(event);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).stream().map(requestMapper::toParticipationRequestDto).toList())
                .rejectedRequests(requestRepository.findAllByEventAndStatus(event, RequestStatus.REJECTED).stream().map(requestMapper::toParticipationRequestDto).toList())
                .build();
    }

    private boolean isStartDateValid(LocalDateTime publicationDate, LocalDateTime startDate, int constraint) {
        long hoursBetween = Duration.between(publicationDate, startDate).toHours();
        return hoursBetween >= constraint;
    }

    private void sendStatisticalData(HttpServletRequest request) {
        EndpointHitDto stat = EndpointHitDto.builder()
                .app(serviceId)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.create(stat);
    }

    private List<ViewStatsDto> convertResponseToList(ResponseEntity<Object> response) {
        if (response.getBody() == null) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert response to list", e);
        }
    }

    private Long getUniqueViews(Event event, String uri) {
        DateTimeFormatter formatter = DATE_TIME_FORMATTER;

        String startDate = event.getCreatedOn().format(formatter);
        String endDate = LocalDateTime.now().format(formatter);
        List<String> uris = List.of(uri);

        List<ViewStatsDto> stats = convertResponseToList(statsClient.getStats(startDate, endDate, uris, true));

        return stats.isEmpty()
                ? 0L
                : stats.stream().mapToLong(ViewStatsDto::getHits).sum();
    }

    private void updateEventFields(Event event, UpdateEventRequest updateEventRequest, StringBuilder updatedFieldsLog) {
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
            updatedFieldsLog.append("Annotation|");
        }
        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d not found", updateEventRequest.getCategory()))));
            updatedFieldsLog.append("Category|");
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
            updatedFieldsLog.append("Description|");
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(updateEventRequest.getLocation());
            updatedFieldsLog.append("Location|");
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
            updatedFieldsLog.append("Paid|");
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
            updatedFieldsLog.append("ParticipantLimit|");
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
            updatedFieldsLog.append("RequestModeration|");
        }
        if (updateEventRequest.getEventDate() != null) {
            validateEventDate(updateEventRequest.getEventDate());
            event.setEventDate(updateEventRequest.getEventDate());
            updatedFieldsLog.append("EventDate|");
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }
    }
}
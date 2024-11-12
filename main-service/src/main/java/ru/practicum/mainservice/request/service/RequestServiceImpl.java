package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.exception.EventParticipationConstraintException;
import ru.practicum.mainservice.exception.exception.NotFoundException;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.ParticipationRequest;
import ru.practicum.mainservice.request.model.RequestStatus;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.util.Util;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));
        
        Collection<ParticipationRequest> requests = requestRepository.findAllByRequester(user);

        if (requests.isEmpty()) {
            return List.of();
        }

        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d not found", eventId)));

        if (event.getInitiator().equals(user)) {
            throw new EventParticipationConstraintException(String.format("User with id=%d is the owner of the event with id=%d.", userId, eventId));
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventParticipationConstraintException(String.format("Event not published. " +
                    "A user with id=%d cannot make a request to participate in an event with id=%d.", userId, eventId));
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new EventParticipationConstraintException(String.format("The event with id=%d has reached the limit(%d) of requests for participation.", eventId, event.getParticipantLimit()));
        }

        RequestStatus status = event.isRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED;

        if (event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(status)
                .build();

        request = requestRepository.save(request);

        if (status.equals(RequestStatus.CONFIRMED)) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Util.userNotFound(userId)));

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d not found", requestId)));

        if (!request.getRequester().equals(user)) {
            throw new NotFoundException(String.format("Request with id=%d not found", requestId));
        }

        request.setStatus(RequestStatus.CANCELED);

        request = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }
}

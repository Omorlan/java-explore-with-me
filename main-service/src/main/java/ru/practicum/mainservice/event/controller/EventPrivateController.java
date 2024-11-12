package ru.practicum.mainservice.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByCurrentUser(@PathVariable("userId") Long userId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get events for user with ID: {}. Params - from: {}, size: {}", userId, from, size);

        List<EventShortDto> events = eventService.getEventsByCurrentUser(userId, from, size);

        log.info("Returning {} events for user with ID: {}", events.size(), userId);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable("userId") Long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Received request to create an event for user with ID: {}. Event data: {}", userId, newEventDto);

        EventFullDto event = eventService.create(userId, newEventDto);

        log.info("Event created successfully for user with ID: {}", userId);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getFullEventsByCurrentUser(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId) {
        log.info("Received request to get full event details for user with ID: {} and event with ID: {}", userId, eventId);

        EventFullDto event = eventService.getFullEventByIdForCurrentUser(userId, eventId);

        log.info("Returning event details for user with ID: {} and event with ID: {}", userId, eventId);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByCurrentUser(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Received request to update event with ID: {} for user with ID: {}. Update data: {}", eventId, userId, updateEventUserRequest);

        EventFullDto updatedEvent = eventService.updateByCurrentUser(userId, eventId, updateEventUserRequest);

        log.info("Event with ID: {} updated successfully for user with ID: {}", eventId, userId);
        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByCurrentUser(@PathVariable("userId") Long userId,
                                                                  @PathVariable("eventId") Long eventId) {
        log.info("Received request to get participation requests for event with ID: {} and user with ID: {}", eventId, userId);

        List<ParticipationRequestDto> requests = eventService.getRequestsByCurrentUser(userId, eventId);

        log.info("Returning {} participation requests for event with ID: {} and user with ID: {}", requests.size(), eventId, userId);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatus(@PathVariable("userId") Long userId,
                                                       @PathVariable("eventId") Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Received request to update status for event with ID: {} and user with ID: {}. Status update: {}", eventId, userId, statusUpdateRequest);

        EventRequestStatusUpdateResult result = eventService.updateStatus(userId, eventId, statusUpdateRequest);

        log.info("Status updated successfully for event with ID: {} and user with ID: {}", eventId, userId);
        return result;
    }
}

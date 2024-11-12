package ru.practicum.mainservice.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.mainservice.util.Util.DATE_TIME_PATTERN;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getFullEvents(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<State> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to get events with params - users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> events = eventService.getFullEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        log.info("Returning {} events.", events.size());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable("eventId") Long eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Received request to update event with ID: {}. Update data: {}", eventId, updateEventAdminRequest);

        EventFullDto updatedEvent = eventService.updateByAdmin(eventId, updateEventAdminRequest);

        log.info("Event with ID: {} updated successfully.", eventId);
        return updatedEvent;
    }
}

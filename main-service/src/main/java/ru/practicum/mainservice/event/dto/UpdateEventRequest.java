package ru.practicum.mainservice.event.dto;

import ru.practicum.mainservice.event.model.Location;

import java.time.LocalDateTime;

public interface UpdateEventRequest {
    String getAnnotation();

    Long getCategory();

    String getDescription();

    LocalDateTime getEventDate();

    Location getLocation();

    Boolean getPaid();

    Long getParticipantLimit();

    Boolean getRequestModeration();

    String getTitle();
}

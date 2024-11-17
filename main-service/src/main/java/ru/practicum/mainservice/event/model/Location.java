package ru.practicum.mainservice.event.model;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Embeddable
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    /**
     * Latitude coordinate of the location.
     */
    Float lat;

    /**
     * Longitude coordinate of the location.
     */
    Float lon;
}
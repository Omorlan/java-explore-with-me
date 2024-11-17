package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Location;
import ru.practicum.mainservice.event.model.StateUser;

import java.time.LocalDateTime;

import static ru.practicum.mainservice.util.Util.DATE_TIME_PATTERN;
import static ru.practicum.mainservice.util.Util.TIME_ZONE_UTC;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest implements UpdateEventRequest {
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    String annotation;
    Long category;
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero
    Long participantLimit;
    Boolean requestModeration;
    StateUser stateAction;
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    String title;
}
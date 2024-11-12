package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.event.model.Location;
import ru.practicum.mainservice.event.model.State;
import ru.practicum.mainservice.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.mainservice.util.Util.DATE_TIME_PATTERN;
import static ru.practicum.mainservice.util.Util.TIME_ZONE_UTC;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    boolean paid;
    long participantLimit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime publishedOn;
    boolean requestModeration;
    State state;
    String title;
    Long views;
}
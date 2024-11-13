package ru.practicum.mainservice.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.request.model.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.mainservice.util.Util.DATE_TIME_PATTERN;
import static ru.practicum.mainservice.util.Util.TIME_ZONE_UTC;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    RequestStatus status;
}
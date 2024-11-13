package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.mainservice.util.Util.DATE_TIME_PATTERN;
import static ru.practicum.mainservice.util.Util.TIME_ZONE_UTC;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotNull
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotNull
    Long category;
    @NotNull
    @NotBlank
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN, timezone = TIME_ZONE_UTC)
    LocalDateTime eventDate;
    @NotNull
    Location location;
    @Builder.Default
    boolean paid = false;
    @Builder.Default
    @PositiveOrZero
    long participantLimit = 0L;
    @Builder.Default
    boolean requestModeration = true;
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    String title;
}
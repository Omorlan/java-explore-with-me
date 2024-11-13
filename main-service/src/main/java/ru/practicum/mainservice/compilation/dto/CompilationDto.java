package ru.practicum.mainservice.compilation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.dto.EventShortDto;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    String title;
    List<EventShortDto> events;
    boolean pinned;

    public CompilationDto(Long id, String title, List<EventShortDto> events, boolean pinned) {
        this.id = id;
        this.title = title;
        this.events = events != null ? events : Collections.emptyList();
        this.pinned = pinned;
    }
}
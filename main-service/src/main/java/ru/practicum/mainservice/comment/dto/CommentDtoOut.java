package ru.practicum.mainservice.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoOut {

    Long id;

    @NotNull
    String text;

    @NotNull
    String authorName;

    @NotNull
    LocalDateTime created;
}

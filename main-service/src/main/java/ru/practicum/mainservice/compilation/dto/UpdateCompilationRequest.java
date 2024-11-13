package ru.practicum.mainservice.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
    String title;
    List<Long> events;
    Boolean pinned;
}
package ru.practicum.mainservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 250, message = "Name must be between 2 and 250 characters")
    String name;
    @NotNull
    @NotEmpty
    @NotBlank
    @Email(message = "Wrong email format")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    String email;
}
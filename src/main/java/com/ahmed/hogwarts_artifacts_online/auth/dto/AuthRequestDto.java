package com.ahmed.hogwarts_artifacts_online.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AuthRequestDto(

        @NotEmpty(message ="username is required")
        @NotBlank
        String userName,
        @NotBlank
        @NotEmpty(message="password is required")
        String password
) {
}

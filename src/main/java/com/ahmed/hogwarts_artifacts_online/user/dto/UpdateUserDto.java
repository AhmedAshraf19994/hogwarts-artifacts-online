package com.ahmed.hogwarts_artifacts_online.user.dto;

import com.ahmed.hogwarts_artifacts_online.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDto (

        @NotEmpty(message = "username is required")
        @NotBlank
        String userName,

        // to do create custom annotation to validate enum Role
        @NotNull
        Role role

) {
}

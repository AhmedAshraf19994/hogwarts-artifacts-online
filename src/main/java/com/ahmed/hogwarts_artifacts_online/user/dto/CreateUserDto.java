package com.ahmed.hogwarts_artifacts_online.user.dto;

import com.ahmed.hogwarts_artifacts_online.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateUserDto (

        @NotEmpty(message = "name is required")
        @NotBlank
        String userName,

        @NotEmpty(message = "password is required")
        @NotBlank
        String password,

        // to do create custom annotation to validate enum Role
        @NotNull
        Role role
) {

}

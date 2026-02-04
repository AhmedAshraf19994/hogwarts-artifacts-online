package com.ahmed.hogwarts_artifacts_online.user.dto;

import com.ahmed.hogwarts_artifacts_online.user.Role;

import java.util.List;

public record UserResponseDto(
        int id,
        String userName,
        Role role

) {
}

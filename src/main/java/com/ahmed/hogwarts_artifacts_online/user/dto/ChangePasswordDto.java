package com.ahmed.hogwarts_artifacts_online.user.dto;

public record ChangePasswordDto(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {
}

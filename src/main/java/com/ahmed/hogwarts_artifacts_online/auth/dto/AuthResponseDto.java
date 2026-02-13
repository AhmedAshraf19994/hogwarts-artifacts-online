package com.ahmed.hogwarts_artifacts_online.auth.dto;

import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;

import java.util.List;

public record AuthResponseDto (
       UserResponseDto user ,
        String accessToken
) {

}

package com.ahmed.hogwarts_artifacts_online.auth;

import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthResponseDto;
import com.ahmed.hogwarts_artifacts_online.secuirty.MyUserPrincipal;
import com.ahmed.hogwarts_artifacts_online.user.User;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service

public class AuthMapper {

    public User toUser (Authentication authentication) {
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        assert myUserPrincipal != null;
        return myUserPrincipal.getUser();
    }

    public MyUserPrincipal toMyUserPrincipal (Authentication authentication) {
        return (MyUserPrincipal) authentication.getPrincipal();
    }

    public AuthResponseDto toAuthResponseDto (UserResponseDto userResponseDto, String token) {
        return new AuthResponseDto(userResponseDto, token);

    }
}

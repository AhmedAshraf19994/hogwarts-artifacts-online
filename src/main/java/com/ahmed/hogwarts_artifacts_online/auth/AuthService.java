package com.ahmed.hogwarts_artifacts_online.auth;

import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthRequestDto;
import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthResponseDto;
import com.ahmed.hogwarts_artifacts_online.secuirty.JwtService;
import com.ahmed.hogwarts_artifacts_online.secuirty.MyUserPrincipal;
import com.ahmed.hogwarts_artifacts_online.user.User;
import com.ahmed.hogwarts_artifacts_online.user.UserMapper;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;

    public AuthResponseDto loginUser(AuthRequestDto authRequestDto) {

        // authenticate the user
        Authentication auth = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(authRequestDto.userName(),authRequestDto.password())
        );

        // get my user entity
        User user = authMapper.toUser(auth);
        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);

        //cast the auth object to principle
        MyUserPrincipal  myUserPrincipal = authMapper.toMyUserPrincipal(auth);
        //create access token
        String accessToken = jwtService.CreateToken(myUserPrincipal);

        return authMapper.toAuthResponseDto(userResponseDto, accessToken);

    }
}

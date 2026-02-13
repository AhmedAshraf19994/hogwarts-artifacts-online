package com.ahmed.hogwarts_artifacts_online.auth;


import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthRequestDto;
import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthResponseDto;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/auth/")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("login")
    public Response<AuthResponseDto> loginUser ( @Valid @RequestBody AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = authService.loginUser(authRequestDto);

        logger.debug("login request  ");
        return Response
                .<AuthResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .data(authResponseDto)
                .message("Login Success")
                .build();
    }
}

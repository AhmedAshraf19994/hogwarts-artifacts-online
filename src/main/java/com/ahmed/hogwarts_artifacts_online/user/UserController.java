package com.ahmed.hogwarts_artifacts_online.user;


import com.ahmed.hogwarts_artifacts_online.system.Response;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Response<UserResponseDto> findUser (@PathVariable("userId") int userId) {
        UserResponseDto userResponseDto = userService.findUserById(userId);
        return Response
                .<UserResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find User Success")
                .data(userResponseDto)
                .build();
    }

    @GetMapping("")
    public Response<List<UserResponseDto>> findAllUsers () {
        List<UserResponseDto> users = userService.findAllUsers();
        return Response
                .<List<UserResponseDto>>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find All Users Success")
                .data(users)
                .build();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<UserResponseDto> saveUser ( @Valid @RequestBody CreateUserDto createUserDto) {
        UserResponseDto userResponseDto = userService.saveUser(createUserDto);
                return Response
                        .<UserResponseDto>builder()
                        .flag(true)
                        .code(HttpStatus.CREATED.value())
                        .message("Save User Success")
                        .data(userResponseDto)
                        .build();
    }

    @PutMapping("/{userId}")
    public Response<UserResponseDto> updateUser (
            @PathVariable("userId") int userId,
            @Valid @RequestBody UpdateUserDto updateUserDto
            ) {
        UserResponseDto userResponseDto = userService.updateUser(userId,updateUserDto);
        return Response
                .<UserResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Update User Success")
                .data(userResponseDto)
                .build();

    }

    @DeleteMapping("/{userId}")
    public Response<?> deleteUser (@PathVariable("userId") int userId) {
        userService.deleteUserById(userId);
        return Response
                .builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Delete User Success")
                .data(null)
                .build();

    }
}

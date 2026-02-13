package com.ahmed.hogwarts_artifacts_online.user;

import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getRole()
        ) ;
    }

    public User toUser(CreateUserDto createUserDto) {
        return User
                .builder()
                .userName(createUserDto.userName())
                .password(createUserDto.password())
                .role(createUserDto.role())
                .isEnabled(true)
                .build();
    }
}

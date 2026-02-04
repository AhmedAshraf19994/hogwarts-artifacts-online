package com.ahmed.hogwarts_artifacts_online.user;


import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto findUserById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        return userMapper.toUserResponseDto(user);
    }

    public List<UserResponseDto> findAllUsers () {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).collect(Collectors.toList());
    }

    public UserResponseDto saveUser (CreateUserDto createUserDto) {
        //password needs to be encoded
        User user = userMapper.toUser(createUserDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    public UserResponseDto updateUser (int userId,UpdateUserDto updateUserDto) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        oldUser.setUserName(updateUserDto.userName());
        oldUser.setRole(updateUserDto.role());
        User savedUser = userRepository.save(oldUser);
        return userMapper.toUserResponseDto(savedUser);
    }

    public void deleteUserById (int userId) {
        User  user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        userRepository.deleteById(userId);
    }
}


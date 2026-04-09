package com.ahmed.hogwarts_artifacts_online.user;

import com.ahmed.hogwarts_artifacts_online.auth.AuthService;
import com.ahmed.hogwarts_artifacts_online.client.jwtTokenWhiteListService.JwtTokenWhiteListService;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ChangePasswordIllegalArgumentException;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.user.dto.ChangePasswordDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthService authService ;

    private final JwtTokenWhiteListService jwtTokenWhiteListService;

    public UserResponseDto findUserById(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        return userMapper.toUserResponseDto(user);
    }

    public List<UserResponseDto> findAllUsers () {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).collect(Collectors.toList());
    }

    public UserResponseDto saveUser (CreateUserDto createUserDto) {
        User user = userMapper.toUser(createUserDto);
        //password needs to be encoded
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    public UserResponseDto updateUser (int userId, UpdateUserDto updateUserDto) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));

        //check if he has admin role so he can update all fields
        if (authService.isAdmin()) {
            oldUser.setUserName(updateUserDto.userName());
            oldUser.setRole(updateUserDto.role());
        } else { // if he has user role he can only update username
            oldUser.setUserName(updateUserDto.userName());
        }

        User savedUser = userRepository.save(oldUser);

        return userMapper.toUserResponseDto(savedUser);
    }

    public void deleteUserById (int userId) {
        User  user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user", userId));
        userRepository.deleteById(userId);
    }

    public void changePassword (int userId, ChangePasswordDto changePasswordDto) {
        // find the user or throw error
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ObjectNotFoundException("user",userId));

        //checks the old password is right
        if (!passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("password is wrong");
        }

        //checks the new password matches confirm new password
        if (!changePasswordDto.newPassword().equals(changePasswordDto.confirmNewPassword())) {
            throw new ChangePasswordIllegalArgumentException("new password does not match confirm new password");
        }

        //checks if the new pass follows the password policy
        String passwordPolicy = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        if(!changePasswordDto.newPassword().matches(passwordPolicy)) {
            throw new ChangePasswordIllegalArgumentException("new password does not follow password policy");
        }

        //hash and  change the password
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        //revoke user old jwt
        jwtTokenWhiteListService.removeToken(userId);

        //save the user
        userRepository.save(user);

    }

}


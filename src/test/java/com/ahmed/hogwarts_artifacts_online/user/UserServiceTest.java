package com.ahmed.hogwarts_artifacts_online.user;

import com.ahmed.hogwarts_artifacts_online.auth.AuthService;
import com.ahmed.hogwarts_artifacts_online.client.jwtTokenWhiteListService.JwtTokenWhiteListService;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ChangePasswordIllegalArgumentException;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.user.dto.ChangePasswordDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(value = "dev")
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthService authService;

    @Mock
     UserMapper userMapper ;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenWhiteListService jwtTokenWhiteListService;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserByIdSuccess() {
        //given
        User user = User.builder().userName("Harry Potter").password("123456").build();
        UserResponseDto userResponseDto= new UserResponseDto(1, "Harry Potter",Role.USER);
        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDto);
        //when
        UserResponseDto result = userService.findUserById(Mockito.any(Integer.class));
        //then
        assertEquals(userResponseDto.id(), result.id());
        assertEquals(userResponseDto.userName(), result.userName());
    }

    @Test
    void findUserByIdFail () {
        //given
        when(userRepository.findById(Mockito.any(Integer.class))).thenThrow(new ObjectNotFoundException("user", 1));
        //when
        Exception exception = assertThrows(ObjectNotFoundException.class,() ->
                userService.findUserById(Mockito.any(Integer.class)));
        //then
        assertEquals("could not find user with id 1", exception.getMessage());
    }

    @Test
    void findAllUsersSuccess () {
        //given
        User userA = User.builder().id(1).userName("Harry Potter").password("123456").build();
        User userB = User.builder().id(2).userName("Albus Dumbledore").password("123456").build();

        List<User> users = new ArrayList<>();
        users.add(userA);
        users.add(userB);
        UserResponseDto userResponseDto= new UserResponseDto(1, "Harry Potter",Role.USER);
        UserResponseDto userResponseDtoOne= new UserResponseDto(1, "Harry Potter",Role.USER);
        UserResponseDto userResponseDtoTwo= new UserResponseDto(1, "Albus Potter",Role.USER);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserResponseDto(Mockito.any(User.class))).thenReturn(userResponseDto);
        //when
        List <UserResponseDto> result = userService.findAllUsers();
        //then
        assertEquals(2, result.size());
    }

    @Test
    void saveUserSuccess () {
        //given
        User user = User.builder().id(1).userName("Harry Potter").role(Role.USER).password("123456").build();
        UserResponseDto userResponseDto= new UserResponseDto(1, "Harry Potter", Role.USER);
        CreateUserDto createUserDto = new CreateUserDto("Harry Potter", "12345", Role.USER);
        when(userMapper.toUser(Mockito.any(CreateUserDto.class))).thenReturn(user);
        when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("Encoded Password");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDto(Mockito.any(User.class))).thenReturn(userResponseDto);
        //when
        UserResponseDto result = userService.saveUser(createUserDto);
        //then
        assertEquals(userResponseDto.id(),result.id());
        assertEquals(userResponseDto.userName(),result.userName());
        assertEquals(userResponseDto.role(),result.role());
    }

    @Test
    void updateUserWithAdminRoleSuccess () {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Albus Dumbledore",Role.ADMIN);

        User oldUser = User.builder().id(1).userName("Harry Potter").role(Role.USER).build();

        User savedUser = User.builder().id(1).userName("Albus Dumbledore").role(Role.ADMIN).build();

        UserResponseDto userResponseDto = new UserResponseDto(1, "Albus Dumbledore",Role.ADMIN);

        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(oldUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(Mockito.any(User.class))).thenReturn(userResponseDto);
        when(authService.isAdmin()).thenReturn(true);

        //when
        UserResponseDto result = userService.updateUser(1,updateUserDto);
        //then
        assertEquals("Albus Dumbledore" ,oldUser.getUserName());
        assertEquals(Role.ADMIN, oldUser.getRole());
        assertEquals(result, userResponseDto);
    }

    @Test
    void updateUserWithUserRoleSuccess () {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Albus Dumbledore",Role.ADMIN);

        User oldUser = User.builder().id(1).userName("Harry Potter").role(Role.USER).build();

        User savedUser = User.builder().id(1).userName("Albus Dumbledore").role(Role.USER).build();

        UserResponseDto userResponseDto = new UserResponseDto(1, "Albus Dumbledore",Role.USER);

        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(oldUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponseDto(Mockito.any(User.class))).thenReturn(userResponseDto);
        when(authService.isAdmin()).thenReturn(false);

        //when
        UserResponseDto result = userService.updateUser(1,updateUserDto);
        //then
        assertEquals("Albus Dumbledore" ,oldUser.getUserName());
        assertEquals(Role.USER, oldUser.getRole());
        assertEquals(result, userResponseDto);
    }

    @Test
    void updateUserFailWithNoUserFound () {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Albus Dumbledore",Role.USER);
        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1, updateUserDto));
        //then
        assertEquals("could not find user with id 1",exception.getMessage());
    }

    @Test
    void deleteUserSuccess () {
        //given
        User user = User.builder().id(1).userName("Harry Potter").role(Role.USER).build();
        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(user.getId());
        //when
        userService.deleteUserById(1);
        //then
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).deleteById(1);
    }
    @Test
    void deleteUserFail () {
        //given
        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.deleteUserById(1));
        //then
        assertEquals("could not find user with id 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void changePasswordSuccess () {
        //given
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("Ab123456", "Bb123456", "Bb123456");
        int userId = 1;
        User oldUser = User.builder().password("encryptedOldPassword").build();
        User newUser = User.builder().password("encryptedNewPassword").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encryptedNewPassword");
        when(userRepository.save(Mockito.any(User.class))).thenReturn(newUser);
        doNothing().when(jwtTokenWhiteListService).removeToken(anyInt());

        //when
        userService.changePassword(userId, changePasswordDto);

        //then
        assertEquals("encryptedNewPassword",oldUser.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(oldUser);

    }

    @Test
    void changePasswordFailWithNoFoundUserById () {
        //given
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("Ab123456", "Bb123456", "Bb123456");
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            userService.changePassword(userId, changePasswordDto);
        });

        //then
        assertEquals("could not find user with id 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void changePasswordFailWithWrongOldPassword () {
        //given
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("Ab123456", "Bb123456", "Bb123456");
        User user = User.builder().password("encryptedPassword").build();
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(false);

        //when
        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            userService.changePassword(userId, changePasswordDto);
        });

        //then
        assertEquals("password is wrong", exception.getMessage());
        verify(userRepository,times(1)).findById(userId);
    }

 @Test
    void changePasswordFailWithNewPasswordDoesNotMatchConfirmNewPassword () {
        //given
         ChangePasswordDto changePasswordDto = new ChangePasswordDto("Ab123456", "Bb123457", "Bb123456");
         User user = User.builder().password("encryptedPassword").build();
         int userId = 1;

         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
         when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);

        //when
         Exception exception = assertThrows(ChangePasswordIllegalArgumentException.class, () -> {
             userService.changePassword(userId, changePasswordDto);
         });

        //then
        assertEquals("new password does not match confirm new password", exception.getMessage());
    }

    @Test
    void changePasswordFailWithNewPasswordDoesNotFollowPasswordPolicy () {
        //given
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("Ab123456", "B123457", "B123457");
        User user = User.builder().password("encryptedPassword").build();
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(),anyString())).thenReturn(true);

        //when
        Exception exception = assertThrows(ChangePasswordIllegalArgumentException.class, () -> {
            userService.changePassword(userId, changePasswordDto);
        });

        //then
        assertEquals("new password does not follow password policy", exception.getMessage());
    }

}
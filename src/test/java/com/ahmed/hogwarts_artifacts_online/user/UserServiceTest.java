package com.ahmed.hogwarts_artifacts_online.user;

import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findUserByIdSuccess() {
        //given
        User user = User.builder().id(1).userName("Harry Potter").password("123456").build();
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
    void updateUserSuccess () {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Albus Dumbledore",Role.USER);
        User oldUser = User.builder().id(1).userName("Harry Potter").role(Role.USER).build();
        UserResponseDto newUser = new UserResponseDto(1, "Albus Dumbledore",Role.USER);

        when(userRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(oldUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(oldUser);
        when(userMapper.toUserResponseDto(Mockito.any(User.class))).thenReturn(newUser);

        //when
        UserResponseDto result = userService.updateUser(1,updateUserDto);
        //then
        assertEquals(updateUserDto.userName(),result.userName());
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



}
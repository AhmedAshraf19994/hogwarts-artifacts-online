package com.ahmed.hogwarts_artifacts_online.user;

import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UserResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@ActiveProfiles(value = "dev")

class UserControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findUserSuccess() throws Exception {
        //given
        UserResponseDto userResponseDto = new UserResponseDto(1, "Harry Potter", Role.USER);
        when(userService.findUserById(Mockito.any(Integer.class))).thenReturn(userResponseDto);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(  baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find User Success"))
                .andExpect(jsonPath("$.data.id").value(userResponseDto.id()) );
    }
    
    @Test
    void findUserFail() throws Exception {
        //given
        when(userService.findUserById(Mockito.any(Integer.class))).thenThrow( new ObjectNotFoundException("user", 1));
        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(  baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find user with id 1"))
                .andExpect(jsonPath("$.data").isEmpty()) ;
    }

    @Test
    void findAllUsersSuccess () throws Exception {
        //given
        User userA = User.builder().id(1).userName("Harry Potter").password("123456").role(Role.USER).build();
        User userB = User.builder().id(2).userName("Albus Dumbledore").password("123456").role(Role.USER).build();

        List<User> users = new ArrayList<>();
        users.add(userA);
        users.add(userB);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(  baseUrl + "/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Users Success"))
                .andExpect(jsonPath("$.data").isArray()) ;

    }

    @Test
    void saveUserSuccess () throws Exception {
        //given
        CreateUserDto createUserDto = new CreateUserDto("Harry Potter", "12345", Role.USER);
        String serializedPayload = objectMapper.writeValueAsString(createUserDto);
        UserResponseDto userResponseDto= new UserResponseDto(1, "Harry Potter", Role.USER);
        when(userService.saveUser(Mockito.any(CreateUserDto.class))).thenReturn(userResponseDto);
        //when then
        mockMvc.perform(post(baseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Save User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(userResponseDto.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(userResponseDto.userName()));
    }

    @Test
    void saveUserFailWithBadInput () throws Exception {
        //given
        CreateUserDto createUserDto = new CreateUserDto(null, null, null);
        String serializedPayload = objectMapper.writeValueAsString(createUserDto);
        //when then
        mockMvc.perform(post(baseUrl + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"))
   ;
    }

    @Test
    void updateUserSuccess () throws Exception {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Harry Potter", Role.USER);
        String serializedPayload = objectMapper.writeValueAsString(updateUserDto);
        UserResponseDto userResponseDto = new UserResponseDto(1, "Harry Potter", Role.USER);
        when(userService.updateUser(Mockito.any(Integer.class), Mockito.any(UpdateUserDto.class))).thenReturn(userResponseDto);
        //when then
        mockMvc.perform(put(baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update User Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.userName").value("Harry Potter"));

    }

    @Test
    void updateUserFailWithNoUserFound () throws Exception {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto("Harry Potter", Role.USER);
        String serializedPayload = objectMapper.writeValueAsString(updateUserDto);
        when(userService.updateUser(Mockito.any(Integer.class), Mockito.any(UpdateUserDto.class))).thenThrow(new ObjectNotFoundException("user", 1));
        //when then
        mockMvc.perform(put(baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find user with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());


    }

    @Test
    void updateUserFailWithBadInput () throws Exception {
        //given
        UpdateUserDto updateUserDto = new UpdateUserDto(null, null);
        String serializedPayload = objectMapper.writeValueAsString(updateUserDto);
        //when then
        mockMvc.perform(put(baseUrl + "/users/{userId}",1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("provided arguments are not valid, check data for details"));


    }

    @Test
    void deleteUserSuccess () throws Exception {
        //given
        doNothing().when(userService).deleteUserById(Mockito.any(Integer.class));
        //when then
        mockMvc.perform(delete(baseUrl + "/users/{userId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete User Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteUserFail () throws Exception {
        //given
        doThrow(new ObjectNotFoundException("user", 1)).when(userService).deleteUserById(1);
        //when then
        mockMvc.perform(delete(baseUrl + "/users/{userId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find user with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

}
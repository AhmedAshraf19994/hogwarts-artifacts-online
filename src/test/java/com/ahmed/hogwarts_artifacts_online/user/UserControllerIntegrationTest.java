package com.ahmed.hogwarts_artifacts_online.user;
import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthRequestDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.ChangePasswordDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.user.dto.UpdateUserDto;
import com.redis.testcontainers.RedisContainer;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional // for reset database after every test
@Testcontainers
@ActiveProfiles(value = "dev")
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    String token;

    @Container
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:6.2.6"));

    //to sign in with a user role .
    private void logInWithUserRolePermission () throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto("John", "56789");
        // get the raw mvc result and print for debug
        String serializedAuthRequestDto = objectMapper.writeValueAsString(authRequestDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedAuthRequestDto)
                .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andReturn();
        //access the response body as string
        String content = mvcResult.getResponse().getContentAsString();
        // transform the string to json object to extract the token value
        JSONObject json = new JSONObject(content);
        // assign the token
        token = "Bearer " + json.getJSONObject("data").getString("accessToken");
    }

    @BeforeEach
    void setUp () throws Exception {
        //sign in with admin role
        AuthRequestDto authRequestDto = new AuthRequestDto("Ahmed", "12345");
        // get the raw mvc result and print for debug
        String serializedAuthRequestDto = objectMapper.writeValueAsString(authRequestDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedAuthRequestDto)
                .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andReturn();
        //access the response body as string
        String content = mvcResult.getResponse().getContentAsString();
        // transform the string to json object to extract the token value
        JSONObject json = new JSONObject(content);
        // assign the token
        token = "Bearer " + json.getJSONObject("data").getString("accessToken");
    }

    @Test
    void findAllUsersSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Users Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(3)));
    }

    @Test
    void findAllUsersFailWithNoPermission () throws Exception {
        AuthRequestDto authRequestDto = new AuthRequestDto("John", "56789");
        // get the raw mvc result and print for debug
        String serializedAuthRequestDto = objectMapper.writeValueAsString(authRequestDto);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedAuthRequestDto)
                .accept(MediaType.APPLICATION_JSON)).andDo(MockMvcResultHandlers.print()).andReturn();
        //access the response body as string
        String content = mvcResult.getResponse().getContentAsString();
        // transform the string to json object to extract the token value
        JSONObject json = new JSONObject(content);
        // assign the token
        token = "Bearer " + json.getJSONObject("data").getString("accessToken");
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"));
    }

 @Test
    void findUserByIdWithRoleAdminAccessingAnyUserInfoSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 1)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value("Ahmed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("ADMIN"));

     mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 2)
                     .header("Authorization", token))
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
             .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
             .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find User Success"))
             .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(2))
             .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value("John"))
             .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("USER"));
    }

    @Test
    void findUserByIdWithRoleUserAccessingHisOwnInfoSuccess () throws Exception {
        //sign in with use role permission
        logInWithUserRolePermission();

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 2)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value("USER"));
    }

    @Test
    void findUserByIdWithRoleUserAccessingOthersInfoFailWithNoPermission () throws Exception {
        //sign in with use role permission
        logInWithUserRolePermission();

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 3)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"));
    }


    @Test
    void findUserByIdFailWithNoFoundUser () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 4)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find user with id 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void findUserByIdFailWithNoPermission () throws Exception {
        //to sign in with user with no permission
        logInWithUserRolePermission();
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users/{userId}", 4)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"));
    }

    @Test
    void saveUserSuccess () throws Exception {
        CreateUserDto createUserDto = new CreateUserDto("test", "test", Role.USER);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(createUserDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Save User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(createUserDto.userName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(createUserDto.role().name()));

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/users")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Users Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", hasSize(4)));
    }

    @Test
    void saveUserFailWithInvalidInput () throws Exception {
        CreateUserDto createUserDto = new CreateUserDto(null, null, null);

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(createUserDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));
    }

    @Test
    void saveUserFailWithNoPermission () throws Exception {
        //sign in with user role
        logInWithUserRolePermission();

        CreateUserDto createUserDto = new CreateUserDto("test", "test", Role.USER);
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(createUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void updateUserWithAdminRoleUpdatingAnyUserInfoSuccess () throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto("new name", Role.ADMIN);

        // updating another user info
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(updateUserDto.userName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(updateUserDto.role().name()));

        //updating own info
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(updateUserDto.userName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(updateUserDto.role().name()));
    }

    @Test
    @DisplayName(value = "check user is only allowed to update his name only")
    void updateUserWithUserRoleUpdatingHisOwnInfoInfoSuccess () throws Exception {
        //login with user role
        logInWithUserRolePermission();

        UpdateUserDto updateUserDto = new UpdateUserDto("new name", Role.ADMIN);

        // updating another user info
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userName").value(updateUserDto.userName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.role").value(Role.USER.name()));
    }

    @Test
    @DisplayName(value = "check user role fails to update other users info with no permission")
    void updateUserWithUserRoleUpdatingOthersInfoInfoFailWithNoPermission () throws Exception {
        //login with user role
        logInWithUserRolePermission();

        UpdateUserDto updateUserDto = new UpdateUserDto("new name", Role.ADMIN);

        // updating another user info
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"));
    }

 @Test
    void updateUserFailWithInvalidInput () throws Exception {
    UpdateUserDto updateUserDto = new UpdateUserDto(null, Role.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));
    }

@Test
    void updateUserFailWithNotFoundUser () throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto("test", Role.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find user with id 4"));
    }

    @Test
    void updateUserFailWithNoPermission () throws Exception {
        // sign in with user role
        logInWithUserRolePermission();

        UpdateUserDto updateUserDto = new UpdateUserDto("test", Role.USER);

        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/users/{userId}", 3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(updateUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

    @Test
    void deleteUserSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/{userId}", 1)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Delete User Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

 @Test
    void deleteUserFailWithNoUserFound () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/{userId}", 4)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find user with id 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

@Test
    void deleteUserFailWithNoPermission () throws Exception {
        //sign in with user role
        logInWithUserRolePermission();

        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/users/{userId}", 1)
                    .header("Authorization", token))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No permission"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("change password success with valid input PATCH")
    void changePasswordSuccess () throws Exception {
        //login with user role
        logInWithUserRolePermission();

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("56789", "Ab123456",
                "Ab123456");

        mockMvc.perform(MockMvcRequestBuilders.patch(baseUrl + "/users/{userId}/password", 2)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(changePasswordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Change Password Success"));
    }

 @Test
 @DisplayName("change password fail with no found user PATCH")
    void changePasswordFailWithNoFoundUser () throws Exception {
     ChangePasswordDto changePasswordDto = new ChangePasswordDto("56789", "Ab123456",
                "Ab123456");

        mockMvc.perform(MockMvcRequestBuilders.patch(baseUrl + "/users/{userId}/password", 4)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(changePasswordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find user with id 4"));
    }

@Test
@DisplayName("change password fail with old password wrong PATCH")
    void changePasswordFailWithOldPasswordIsWrong () throws Exception {
    //login with user role
    logInWithUserRolePermission();

    ChangePasswordDto changePasswordDto = new ChangePasswordDto("wrongPassword", "Ab123456",
                "Ab123456");

        mockMvc.perform(MockMvcRequestBuilders.patch(baseUrl + "/users/{userId}/password", 2)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(changePasswordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()));
    }

@Test
@DisplayName("change password fail with new password does not match confirm new password PATCH")
    void changePasswordFailWithNewPasswordDoesNotMatchNewPassword () throws Exception {
        //login with user role
        logInWithUserRolePermission();

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("56789", "Ab123456",
                "Ab123457");

        mockMvc.perform(MockMvcRequestBuilders.patch(baseUrl + "/users/{userId}/password",2)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(changePasswordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("new password does not match confirm new password"));
    }

@Test
@DisplayName("change password fail with new password does not follow password policy PATCH")
    void changePasswordFailWithNewPasswordDoesNotFollowPasswordPolicy() throws Exception {
        //login with user role
        logInWithUserRolePermission();

        ChangePasswordDto changePasswordDto = new ChangePasswordDto("56789", "A123456",
                "A123456");

        mockMvc.perform(MockMvcRequestBuilders.patch(baseUrl + "/users/{userId}/password",2)
                        .header("Authorization", token)
                .content(objectMapper.writeValueAsString(changePasswordDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("new password does not follow password policy"));
    }






}

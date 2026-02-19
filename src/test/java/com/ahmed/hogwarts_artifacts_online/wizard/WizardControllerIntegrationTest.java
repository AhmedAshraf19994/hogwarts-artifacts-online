package com.ahmed.hogwarts_artifacts_online.wizard;


import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthRequestDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // for setting the database after every test
@ActiveProfiles(value = "dev")

public class WizardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    String token;

    @BeforeEach
    void setUp () throws Exception {
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
    void findAllWizardsSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Wizards Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(3)));
    }

    @Test
    void findWizardByIdSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/{wizardId}", 1)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find Wizard Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Harry Potter"));
    }

    @Test
    void findWizardByIFailWithNotFoundWizard () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/{wizardId}", 4)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find wizard with id 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

    }

    @Test
    void saveWizardSuccess () throws Exception {
        CreateWizardDto createWizardDto =  new CreateWizardDto("test");
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWizardDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Save Wizard Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(createWizardDto.name()));

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Wizards Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(4)));

    }

    @Test
    void saveWizardFailWithInvalidInput () throws Exception {
        CreateWizardDto createWizardDto =  new CreateWizardDto("");
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWizardDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));

    }

    @Test
    void updateWizardSuccess () throws Exception {
        CreateWizardDto createWizardDto =  new CreateWizardDto("test");
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/{wizardId}", 1)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWizardDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update Wizard Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("test"));
    }

    @Test
    void updateWizardFailWithNotFoundWizard () throws Exception {
        CreateWizardDto createWizardDto =  new CreateWizardDto("test");
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/{wizardId}", 4)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWizardDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find wizard with id 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void updateWizardFailWithInvalidInput () throws Exception {
        CreateWizardDto createWizardDto =  new CreateWizardDto(null);
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/{wizardId}", 4)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWizardDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));
    }

    @Test
    void deleteWizardSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/{wizardId}",1)
                .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(("$.flag")).value(true))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.code")).value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.message")).value("Delete Wizard Success"))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data")).isEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards")
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Wizards Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(2)));
    }

    @Test
    void deleteWizardFailWithNotFoundWizard () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/{wizardId}", 5)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath(("$.flag")).value(false))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.code")).value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.message")).value("could not find wizard with id 5"))
                .andExpect(MockMvcResultMatchers.jsonPath(("$.data")).isEmpty());
    }

}

package com.ahmed.hogwarts_artifacts_online.artifact;


import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.auth.dto.AuthRequestDto;
import jakarta.transaction.Transactional;
import lombok.val;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // to rest any changes made to the database after every test
public class ArtifactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    String token;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

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
    void findAllArtifactsSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(6)));
    }

    @Test
    void findArtifactByIdSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts/{artifactId}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Resurrection Stone"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value("the Resurrection Stone had the power to bring back lost loved ones."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value("imageUrl"));
    }

    @Test
    void findArtifactByIdFail () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts/{artifactId}",9)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 9"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void saveArtifactSuccess () throws Exception {
        CreateArtifactDto createArtifactDto = new CreateArtifactDto("Gorgon Portrait",
                "A pair of Vanishing Cabinets would act as a passage between two places.",
                "imageUrl");
        String serializedCreateArtifactDto = objectMapper.writeValueAsString(createArtifactDto);
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                .content(serializedCreateArtifactDto)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Save Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(7))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(createArtifactDto.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(createArtifactDto.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(createArtifactDto.imageUrl()));

        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(7)));
    }

    @Test
    void saveArtifactFailWithInvalidInput () throws Exception {
        CreateArtifactDto createArtifactDto = new CreateArtifactDto( null,
                null,
                null);
        String serializedCreateArtifactDto = objectMapper.writeValueAsString(createArtifactDto);
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(serializedCreateArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));
    }

    @Test
    void updateArtifactSuccess () throws Exception {
        CreateArtifactDto updateArtifactDto = new CreateArtifactDto("Gorgon Portrait",
                "A pair of Vanishing Cabinets would act as a passage between two places.",
                "imageUrl");
        String serializedCreateArtifactDto = objectMapper.writeValueAsString(updateArtifactDto);
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/{artifactId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(serializedCreateArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(updateArtifactDto.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(updateArtifactDto.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(updateArtifactDto.imageUrl()));
    }

     @Test
    void updateArtifactFailWithInvalidInput () throws Exception {
        CreateArtifactDto updateArtifactDto = new CreateArtifactDto(null,
                "",
                null);
        String serializedCreateArtifactDto = objectMapper.writeValueAsString(updateArtifactDto);
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/{artifactId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(serializedCreateArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"));
    }

    @Test
    void updateArtifactFailWithNotFoundArtifact () throws Exception {
        CreateArtifactDto updateArtifactDto = new CreateArtifactDto("Gorgon Portrait",
                "A pair of Vanishing Cabinets would act as a passage between two places.",
                "imageUrl");
        String serializedCreateArtifactDto = objectMapper.writeValueAsString(updateArtifactDto);
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/{artifactId}", 9)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", token)
                        .content(serializedCreateArtifactDto)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 9"));
    }

    @Test
    void deleteArtifactByIdSuccess () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/artifacts/{artifactId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Delete Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(5)));
    }
    @Test
    void deleteArtifactByIdFailWithNotFoundArtifact () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/artifacts/{artifactId}", 9)
                .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", token))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 9"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get( baseUrl + "/artifacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data",hasSize(6)));
    }


}

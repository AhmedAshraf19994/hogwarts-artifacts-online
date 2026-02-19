package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
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
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest (ArtifactController.class)
@ActiveProfiles(value = "dev")

class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtifactService artifactService;

    @MockitoBean
    ArtifactMapper artifactMapper;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts = new ArrayList<>();
    List<ArtifactResponseDto> returnedArtifacts = new ArrayList<>();



    @BeforeEach
    void setUp() {
        Artifact artifactOne = Artifact.builder().name("Resurrection Stone")
                .description("the Resurrection Stone had the power to bring back lost loved ones.")
                .imageUrl("imageUrl").build();
        Artifact artifactTwo = Artifact.builder().name("Cloak of Invisibility")
                .description("magical garment that renders the wearer unseen.")
                .imageUrl("imageUrl").build();
        artifacts.add(artifactOne);
        artifacts.add(artifactTwo);
        returnedArtifacts = artifacts.stream()
                .map(artifactMapper::toArtifactResponseDto)
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findArtifactByIdSuccess () throws Exception {
        //given
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(1,
                "Resurrection Stone",
                "the Resurrection Stone had the power to bring back lost loved ones.",
                "imageUrl",
                null);

        when(artifactService.findArtifactById(Mockito.any(Integer.class))).thenReturn(artifactResponseDto);

        //when and then
        mockMvc.perform(MockMvcRequestBuilders
                .get(baseUrl + "/artifacts/{artifactId}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find Artifact Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Resurrection Stone"))
                .andExpect(jsonPath("$.data.description").value("the Resurrection Stone had the power to bring back lost loved ones."))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl"))

        ;

    }

    @Test
    void findArtifactByIdFail () throws Exception {
        //given
        when(artifactService.findArtifactById(1)).thenThrow(new ObjectNotFoundException("artifact",1));

        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts/{artifactId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 1"));

    }

    @Test
    void findAllArtifactsSuccess () throws Exception {
        //given
        when(artifactService.findAllArtifacts()).thenReturn(returnedArtifacts)
        ;
        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find All Artifacts Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    void saveArtifactSuccess () throws Exception {
        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "Resurrection Stone",
                "the Resurrection Stone had the power to bring back lost loved ones.",
                "imageUrl");
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(
                1,
                "Resurrection Stone",
                "the Resurrection Stone had the power to bring back lost loved ones.",
                "imageUrl",
                null);
        when(artifactService.saveArtifact(Mockito.any(CreateArtifactDto.class))).thenReturn(artifactResponseDto);
        String serializedPayLoad = objectMapper.writeValueAsString(createArtifactDto);


        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayLoad))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Save Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(artifactResponseDto.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(artifactResponseDto.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(artifactResponseDto.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(artifactResponseDto.imageUrl()));

    }

    @Test
    void saveArtifactFailWithBadInput () throws Exception {
        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(null,null, null);
        String serializedPayLoad = objectMapper.writeValueAsString(createArtifactDto);


        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/artifacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayLoad))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"))

        ;


    }

    @Test
    void updateArtifactSuccess () throws Exception {
        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "updated name",
                "A basin used to review memories.",
                "imageUrl"
        );
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(
                1,
                "updated name",
                "A basin used to review memories.",
                "imageUrl",
                null

        );
        when(artifactService.updateArtifact(artifactResponseDto.id(),createArtifactDto)).thenReturn(artifactResponseDto);
        String serializedPayLoad = objectMapper.writeValueAsString(createArtifactDto);

        //when then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/{artifactId}",artifactResponseDto.id())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayLoad))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(artifactResponseDto.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(createArtifactDto.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(createArtifactDto.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl").value(createArtifactDto.imageUrl()));


    }

    @Test
    void updateArtifactFail () throws Exception {
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "updated name",
                "A basin used to review memories.",
                "imageUrl"
        );
        int artifactId = 1;

        when(artifactService.updateArtifact(artifactId,createArtifactDto)).thenThrow(new ObjectNotFoundException("artifact",artifactId));
        String serializedPayLoad = objectMapper.writeValueAsString(createArtifactDto);

        //when then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/artifacts/{artifactId}",artifactId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedPayLoad))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 1"));
    }

    @Test
    void deleteArtifactSuccess () throws Exception {
        int artifactId = 1;
        doNothing().when(artifactService).deleteArtifact(1);
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/artifacts/{artifactId}",1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Delete Artifact Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteArtifactFail () throws Exception {
        int artifactId = 1;
        doThrow(new ObjectNotFoundException("artifact",artifactId)).when(artifactService).deleteArtifact(artifactId);
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/artifacts/{artifactId}",1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

}
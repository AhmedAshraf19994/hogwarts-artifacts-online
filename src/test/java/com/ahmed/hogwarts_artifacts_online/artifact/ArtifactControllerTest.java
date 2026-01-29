package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest (ArtifactController.class)
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtifactService artifactService;

    @MockitoBean
    ArtifactMapper artifactMapper;

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

//    @Test
//    void findArtifactByIdSuccess () throws Exception {
//        //given
//        Artifact artifact = Artifact
//                .builder()
//                .id(1)
//                .name("sword")
//                .description("some description")
//                .imageUrl("imageUrl").build();
//        ArtifactResponseDto returnArtifact = artifactMapper.toArtifactResponseDto(artifact);
//        when(artifactService.findArtifactById(artifact.getId())).thenReturn(returnArtifact);
//
//        //when and then
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/api/v1/artifacts/{artifactId}",1)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.flag").value(true))
//                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.message").value("Find Artifact Success"))
//                .andExpect(jsonPath("$.data.id").value(1))
//                .andExpect(jsonPath("$.data.name").value("sword"))
//                .andExpect(jsonPath("$.data.description").value("some description"))
//                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl"))
//
//        ;
//
//    }
//
    @Test
    void findArtifactByIdFail () throws Exception {
        //given
        when(artifactService.findArtifactById(1)).thenThrow(new ArtifactNotFoundException(1));

        //when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/{artifactId}", 1)
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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts").accept(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/artifacts")
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


        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/artifacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayLoad))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("provided arguments are not valid, check data for details"))

        ;

        //then
    }

}
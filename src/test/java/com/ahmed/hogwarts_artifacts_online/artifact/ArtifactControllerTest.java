package com.ahmed.hogwarts_artifacts_online.artifact;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest (ArtifactController.class)
class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtifactService artifactService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findArtifactByIdSuccess () throws Exception {
        //given
        Artifact artifact = Artifact
                .builder()
                .id(1)
                .name("sword")
                .description("some description")
                .imageUrl("imageUrl").build();
        when(artifactService.findArtifactById(artifact.getId())).thenReturn(artifact);

        //when and then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/artifacts/{artifactId}",1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value("200 OK"))
                .andExpect(jsonPath("$.message").value("Find Artifact Success"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("sword"))
                .andExpect(jsonPath("$.data.description").value("some description"))
                .andExpect(jsonPath("$.data.imageUrl").value("imageUrl"))

        ;

    }

    @Test
    void findArtifactByIdFail () throws Exception {
        //given
        when(artifactService.findArtifactById(1)).thenThrow(new ArtifactNotFoundException(1));


        //when then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/{artifactId}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404 NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("could not find artifact with id 1"));



        //then


    }
}
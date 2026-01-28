package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ArtifactServiceTest {
    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    ArtifactService artifactService;
    List<Artifact> artifacts = new ArrayList<>();


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        Artifact artifactOne = Artifact.builder().name("Resurrection Stone")
                .description("the Resurrection Stone had the power to bring back lost loved ones.")
                .imageUrl("imageUrl").build();
        Artifact artifactTwo = Artifact.builder().name("Cloak of Invisibility")
                .description("magical garment that renders the wearer unseen.")
                .imageUrl("imageUrl").build();
        artifacts.add(artifactTwo);
        artifacts.add(artifactOne);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findArtifactByIdSuccess() {
        //given
        Artifact artifact = Artifact
                .builder()
                .id(1)
                .name("stone")
                .description("stone that gives special power for healing")
                .imageUrl("imageUrl")
                .build();
        Wizard wizard = Wizard.builder().id(1).name("Harry Potter").build();
        artifact.setWizard(wizard);
        when(artifactRepository.findById(1)).thenReturn(Optional.of(artifact));

        //when
        ArtifactResponseDto returnedArtifact = artifactService.findArtifactById(1);

        //then
        assertEquals(returnedArtifact, artifact);
        Mockito.verify(artifactRepository, times(1)).findById(1);


    }

    @Test
    void findArtifactByIdNotFound () {

        //given
        when(artifactRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(ArtifactNotFoundException.class,() -> {

            artifactService.findArtifactById(1);
        });

        //then
        assertEquals("could not find artifact with id 1", exception.getMessage());


    }

    @Test
    void findAllArtifactsSuccess () {
        //given
        when(artifactRepository.findAll()).thenReturn(artifacts);
        //when
        List<ArtifactResponseDto> returnedArtifacts = artifactService.findAllArtifacts();
        //then
        assertEquals(returnedArtifacts.size(),artifacts.size());

    }
}
package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles(value = "dev")

class ArtifactServiceTest {
    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    ArtifactMapper artifactMapper;



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
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(
                1,
                "stone",
                "stone that gives special power for healing",
                "imageUrl",
                null);
        Wizard wizard = Wizard.builder().id(1).name("Harry Potter").build();
        artifact.setWizard(wizard);
        when(artifactRepository.findById(1)).thenReturn(Optional.of(artifact));
        when(artifactMapper.toArtifactResponseDto(Mockito.any(Artifact.class))).thenReturn(artifactResponseDto);

        //when
        ArtifactResponseDto returnedArtifact = artifactService.findArtifactById(1);

        //then
        assertEquals(returnedArtifact.id(), artifact.getId());
        assertEquals(returnedArtifact.description(), artifactResponseDto.description());
        assertEquals(returnedArtifact.name(), artifactResponseDto.name());
        assertEquals(returnedArtifact.imageUrl(), artifactResponseDto.imageUrl());
        Mockito.verify(artifactRepository, times(1)).findById(1);


    }

    @Test
    void findArtifactByIdNotFound () {

        //given
        when(artifactRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(ObjectNotFoundException.class,() -> {

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

    @Test
    void saveArtifactSuccess () {

        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "The Pensieve",
                "A basin used to review memories.",
                "imageUrl"
        );
        Artifact artifact = Artifact.builder()
                .id(7)
                .name("The Pensieve")
                .description("A basin used to review memories.")
                .imageUrl("imageUrl").build();
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(
                7,
                "The Pensieve",
                "A basin used to review memories.",
                "imageUrl",
                null
        );
        when(artifactMapper.toArtifact(Mockito.any(CreateArtifactDto.class))).thenReturn(artifact);
        when(artifactRepository.save(Mockito.any(Artifact.class))).thenReturn(artifact);
        when(artifactMapper.toArtifactResponseDto(Mockito.any(Artifact.class))).thenReturn(artifactResponseDto);

        //when
        ArtifactResponseDto returnedArtifact = artifactService.saveArtifact(createArtifactDto);

        //then
        assertEquals(artifact.getId(), returnedArtifact.id());
        assertEquals(artifact.getName(), returnedArtifact.name());
        assertEquals(artifact.getDescription(), returnedArtifact.description());
        assertEquals(artifact.getImageUrl(), returnedArtifact.imageUrl());
        verify(artifactRepository, times(1)).save(artifact);
    }

    @Test
    void updateArtifactSuccess () {
        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "updated name",
                "A basin used to review memories.",
                "imageUrl"
        );
        Artifact artifact = Artifact.builder()
                .id(7)
                .name("The Pensieve")
                .description("A basin used to review memories.")
                .imageUrl("imageUrl").build();
        Artifact updatedArtifact = Artifact.builder()
                .id(7)
                .name("updated name")
                .description("A basin used to review memories.")
                .imageUrl("imageUrl").build();
        ArtifactResponseDto artifactResponseDto = new ArtifactResponseDto(
                7,
                "updated name",
                "A basin used to review memories.",
                "imageUrl",
                null
        );
        when(artifactRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(artifact));
        when(artifactRepository.save(Mockito.any(Artifact.class))).thenReturn(updatedArtifact);
        when(artifactMapper.toArtifactResponseDto(Mockito.any(Artifact.class))).thenReturn(artifactResponseDto);

        //when
        ArtifactResponseDto result = artifactService.updateArtifact(7,createArtifactDto);
        //then
        assertEquals(createArtifactDto.name(), result.name());
        assertEquals(createArtifactDto.description(), result.description());
        assertEquals(createArtifactDto.imageUrl(), result.imageUrl());
    }

    @Test
    void updateArtifactFail() {
        //given
        CreateArtifactDto createArtifactDto = new CreateArtifactDto(
                "The Pensieve",
                "A basin used to review memories.",
                "imageUrl"
        );
        when(artifactRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ObjectNotFoundException.class,() -> {

            artifactService.updateArtifact(1,createArtifactDto);
        });
        //then
        assertEquals("could not find artifact with id 1",exception.getMessage());
    }

    @Test
    void shouldDeleteArtifactSuccess () {
        //when
        Artifact artifact = Artifact.builder()
                .id(1)
                .name("The Pensieve")
                .description("A basin used to review memories.")
                .imageUrl("imageUrl").build();
        when(artifactRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(artifact));
        doNothing().when(artifactRepository).deleteById(artifact.getId());

        // when
        artifactService.deleteArtifact(artifact.getId());
        //then
        verify(artifactRepository, times(1)).deleteById(artifact.getId());




    }

    @Test
    void shouldDeleteArtifactFail () {
        //when
        Artifact artifact = Artifact.builder()
                .id(1)
                .name("The Pensieve")
                .description("A basin used to review memories.")
                .imageUrl("imageUrl").build();
        when(artifactRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());

        // when
        assertThrows(ObjectNotFoundException.class,() -> {
            artifactService.deleteArtifact(artifact.getId());
        });
        //then
        verify(artifactRepository, times(1)).findById(artifact.getId());




    }
    }



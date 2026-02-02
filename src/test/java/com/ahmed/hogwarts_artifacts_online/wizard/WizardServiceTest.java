package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;
    @Mock
    Wizard wizard;
    @Mock
    WizardMapper wizardMapper;
    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards = new ArrayList<>();



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Wizard wizardOne = Wizard.builder().name("Harry Potter").build();
        Wizard wizardTwo = Wizard.builder().name("Hermione Granger").build();
        Wizard wizardThree = Wizard.builder().name("Albus Dumbledore").build();
        wizards.add(wizardOne);
        wizards.add(wizardTwo);
        wizards.add(wizardThree);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findWizardById() {
        //given
        Wizard wizard = Wizard.builder().id(1).name("Harry Potter").build();
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        when(wizardRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(wizard));
        when(wizardMapper.toWizardResponseDto(Mockito.any(Wizard.class))).thenReturn(wizardResponseDto);
        //when
        WizardResponseDto result = wizardService.findWizardById(1);
        //then
        assertEquals(result.id(),wizardResponseDto.id());
        assertEquals(result.name(),wizardResponseDto.name());
        assertEquals(result.artifactsNumber(),wizardResponseDto.artifactsNumber());
    }

    @Test
    void findWizardByIdFail () {
        int wizardId = 1 ;
        //given
        when(wizardRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        //then
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
            wizardService.findWizardById(1);
        });
        //when
        assertEquals("could not find wizard with id " + wizardId, exception.getMessage());
    }

    @Test
    void findAllWizardsSuccess () {
        //given
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter",6);
        when(wizardRepository.findAll()).thenReturn(wizards);
        when(wizardMapper.toWizardResponseDto(Mockito.any(Wizard.class))).thenReturn(wizardResponseDto);
        //when
        List<WizardResponseDto> result = wizardService.findAllWizards();

        //then
        assertEquals(wizards.size(),result.size());
        assertEquals(wizardResponseDto.id(),result.get(0).id());
        assertEquals(wizardResponseDto.name(),result.get(0).name());
        assertEquals(wizardResponseDto.artifactsNumber(),result.get(0).artifactsNumber());

    }

    @Test
    void saveWizardSuccess () {
        //given
        CreateWizardDto createWizardDto = new CreateWizardDto("Harry Potter");
        Wizard wizard = Wizard.builder().id(1).name("Harry Potter").build();
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 0);
        when(wizardMapper.toWizard(createWizardDto)).thenReturn(wizard);
        when(wizardRepository.save(Mockito.any(Wizard.class))).thenReturn(wizard);
        when(wizardMapper.toWizardResponseDto(Mockito.any(Wizard.class))).thenReturn(wizardResponseDto);
        //when
        WizardResponseDto result = wizardService.saveWizard(createWizardDto);
        //then
        assertEquals(result.id(),wizardResponseDto.id());
        assertEquals(result.name(),wizardResponseDto.name());
        assertEquals(result.artifactsNumber(),wizardResponseDto.artifactsNumber());

    }

    @Test
    void updateWizardSuccess () {
        //given
        int wizardId = 1;
        CreateWizardDto createWizardDto = new CreateWizardDto("Harmony Gringer");
        Wizard wizard = Wizard.builder().name("Harry Potter").build();
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harmony Gringer", 2);
         when(wizardRepository.findById(wizardId)).thenReturn(Optional.of(wizard));
        when(wizardRepository.save(Mockito.any(Wizard.class))).thenReturn(wizard);
        when(wizardMapper.toWizardResponseDto(Mockito.any(Wizard.class))).thenReturn(wizardResponseDto);
        //when
        WizardResponseDto result = wizardService.updateWizard(wizardId,createWizardDto);
        //then
        assertEquals(wizardId, result.id());
        assertEquals(wizardResponseDto.name(), result.name());
        assertEquals(wizardResponseDto.artifactsNumber(), result.artifactsNumber());
    }

    @Test
    void updateWizardFail() {
        //given
        int wizardId = 1;
        CreateWizardDto createWizardDto = new CreateWizardDto("Harry Potter");
        when(wizardRepository.findById(wizardId)).thenThrow( new ObjectNotFoundException("wizard", wizardId));

        //when
        Exception exception = assertThrows(ObjectNotFoundException.class, () ->
                wizardService.updateWizard(wizardId, createWizardDto));
        //then
        assertEquals("could not find wizard with id 1", exception.getMessage());
    }

    @Test
    void deleteWizardSuccess () {
        //given
        int wizardId = 1 ;
        Wizard wizard = Wizard.builder().id(1).name("Harry Potter").artifacts(new ArrayList<>()).build();
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        when(wizardRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.of(wizard));
        doNothing().when(wizardRepository).deleteById(wizardId);
        //when
        wizardService.deleteWizard(wizardId);
        //then
        verify(wizardRepository, times(1)).findById(wizardId);
        verify(wizardRepository, times(1)).deleteById(wizardId);
    }

    @Test
    void deleteWizardFail () {
        //given
        int wizardId = 1;
        when(wizardRepository.findById(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        //when
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> wizardService.deleteWizard(wizardId));
        //then
        assertEquals("could not find wizard with id 1", exception.getMessage());
        verify(wizardRepository, times(1)).findById(wizardId);
    }


}

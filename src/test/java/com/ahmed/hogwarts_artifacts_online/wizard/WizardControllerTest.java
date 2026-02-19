package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
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
import org.springframework.test.context.bean.override.mockito.MockitoBeans;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(WizardController.class)
@ActiveProfiles(value = "dev")

class WizardControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper ;
    @MockitoBean
    WizardService wizardService;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    List<WizardResponseDto> wizards  = new ArrayList<>();



    @BeforeEach
    void setUp() {
        WizardResponseDto wizardOne = new WizardResponseDto(1, "Harry Potter", 5);
        WizardResponseDto wizardTwo = new WizardResponseDto(1, "Hermione Granger", 5);
        WizardResponseDto wizardThree = new WizardResponseDto(1, "Albus Dumbledore", 5);
        wizards.add(wizardOne);
        wizards.add(wizardTwo);
        wizards.add(wizardThree);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findWizardByIdSuccess() throws Exception {
        //given
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        when(wizardService.findWizardById(Mockito.any(Integer.class))).thenReturn(wizardResponseDto);

        // then when
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl +"/wizards/{wizardId}", wizardResponseDto.id())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect( jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find Wizard Success"))
                .andExpect(jsonPath("$.data.name").value(wizardResponseDto.name()))
                .andExpect(jsonPath("$.data.artifactsNumber").value(wizardResponseDto.artifactsNumber()));

    }

    @Test
    void findWizardByIdFail() throws Exception {
        //given
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        when(wizardService.findWizardById(Mockito.any(Integer.class))).thenThrow(new ObjectNotFoundException("wizard",wizardResponseDto.id()));

        // then when
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards/{wizardId}", wizardResponseDto.id())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect( jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find wizard with id " + wizardResponseDto.id()))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void findAllWizardsSuccess () throws Exception {
        //given
        when(wizardService.findAllWizards()).thenReturn(wizards);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Find All Wizards Success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void saveWizardSuccess () throws Exception {
        //given
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        CreateWizardDto createWizardDto = new CreateWizardDto("Harry Potter");
        String serializedPayload = objectMapper.writeValueAsString(createWizardDto);
        when(wizardService.saveWizard(Mockito.any(CreateWizardDto.class))).thenReturn(wizardResponseDto);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Save Wizard Success"))
                .andExpect(jsonPath("$.data.id").value(wizardResponseDto.id()))
                .andExpect(jsonPath("$.data.name").value(wizardResponseDto.name()))
                .andExpect(jsonPath("$.data.artifactsNumber").value(wizardResponseDto.artifactsNumber()));

    }

    @Test
    void saveWizardFailWithInvalidInput () throws Exception {
        //given
        CreateWizardDto createWizardDto = new CreateWizardDto(null);
        String serializedPayload = objectMapper.writeValueAsString(createWizardDto);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/wizards")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("provided arguments are not valid, check data for details"));


    }

    @Test
    void updateWizardSuccess () throws Exception {
        //given
        CreateWizardDto createWizardDto = new CreateWizardDto("Harry Potter");
        WizardResponseDto wizardResponseDto = new WizardResponseDto(1, "Harry Potter", 2);
        int wizardId = 1 ;
        String serializedPayload = objectMapper.writeValueAsString(createWizardDto);
        when(wizardService.updateWizard(wizardId,createWizardDto)).thenReturn(wizardResponseDto);
        //when then
        mockMvc.perform(put(baseUrl + "/wizards/{wizardId}", wizardId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Update Wizard Success"))
                .andExpect(jsonPath("$.data.id").value(wizardResponseDto.id()))
                .andExpect(jsonPath("$.data.name").value(wizardResponseDto.name()))
                .andExpect(jsonPath("$.data.artifactsNumber").value(wizardResponseDto.artifactsNumber()));

    }

    @Test
    void updateWizardFailWithNoFoundWizard  () throws Exception {
        CreateWizardDto createWizardDto = new CreateWizardDto("Harry Potter");
        int wizardId = 1 ;
        String serializedPayload = objectMapper.writeValueAsString(createWizardDto);
        when(wizardService.updateWizard(wizardId,createWizardDto)).thenThrow(new ObjectNotFoundException("wizard", wizardId));
        //when then
        mockMvc.perform(put(baseUrl + "/wizards/{wizardId}", wizardId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find wizard with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void updateWizardFailWithBadInput () throws Exception  {
        CreateWizardDto createWizardDto = new CreateWizardDto(null);
        int wizardId = 1 ;
        String serializedPayload = objectMapper.writeValueAsString(createWizardDto);
        //when then
        mockMvc.perform(put(baseUrl + "/wizards/{wizardId}", wizardId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("provided arguments are not valid, check data for details"));

    }

    @Test
    void deleteWizardSuccess () throws Exception {
        //given
        int wizardId = 1;
        doNothing().when(wizardService).deleteWizard(1);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/{wizardId}", wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Delete Wizard Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void deleteWizardFail () throws Exception {
        int wizardId = 1;
        doThrow(new ObjectNotFoundException("wizard",wizardId)).when(wizardService).deleteWizard(1);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.delete(baseUrl + "/wizards/{wizardId}", wizardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find wizard with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void assignArtifactSuccess () throws Exception {
        //given
        doNothing().when(wizardService).assignArtifact(1,2);

        //when then
        mockMvc.perform(put(baseUrl + "/wizards/{wizardId}/artifacts/{artifactId}", 1,2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Assign Artifact Success"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void assignArtifactFailWithNoFoundWizard () throws Exception {
        //given
        doThrow(new ObjectNotFoundException("wizard", 1)).when(wizardService).assignArtifact(1,2);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/{wizardId}/artifacts/{artifactId}", 1,2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find wizard with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }

    @Test
    void assignArtifactFailWithNoFoundArtifact () throws Exception {
        //given
        doThrow(new ObjectNotFoundException("artifact", 1)).when(wizardService).assignArtifact(1,2);
        //when then
        mockMvc.perform(MockMvcRequestBuilders.put(baseUrl + "/wizards/{wizardId}/artifacts/{artifactId}", 1,2)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("could not find artifact with id 1"))
                .andExpect(jsonPath("$.data").isEmpty());

    }
}
package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.system.Response;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/wizards")

public class WizardController {

    private final WizardService wizardService;

    @GetMapping("/{wizardId}")
    public Response<WizardResponseDto> findWizardById (@PathVariable("wizardId") int wizardId) {

        WizardResponseDto wizardResponseDto = wizardService.findWizardById(wizardId);
        return Response
                .<WizardResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find Wizard Success")
                .data(wizardResponseDto)
                .build();
    }

    @GetMapping("")
    public  Response<List<WizardResponseDto>> findAllWizards() {
        List<WizardResponseDto> wizards =  wizardService.findAllWizards();
        return Response
                .<List<WizardResponseDto>>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find All Wizards Success")
                .data(wizards)
                .build();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<WizardResponseDto> saveWizard ( @Valid @RequestBody CreateWizardDto createWizardDto) {
        WizardResponseDto wizardResponseDto = wizardService.saveWizard(createWizardDto);
        return Response
                .<WizardResponseDto>builder()
                .flag(true)
                .code(HttpStatus.CREATED.value())
                .message("Save Wizard Success")
                .data(wizardResponseDto)
                .build();
    }

    @PutMapping ("/{wizardId}")
    public Response<WizardResponseDto> updateWizard (
            @PathVariable("wizardId") int wizardId,
            @Valid @RequestBody CreateWizardDto createWizardDto
    ) {
        WizardResponseDto wizardResponseDto = wizardService.updateWizard(wizardId, createWizardDto);
        return Response
                .<WizardResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Update Wizard Success")
                .data(wizardResponseDto)
                .build();
    }

    @DeleteMapping("/{wizardId}")
    public  Response<?> deleteWizard (@PathVariable("wizardId") int wizardId) {
        wizardService.deleteWizard(wizardId);
        return
                Response
                        .builder()
                        .flag(true)
                        .code(HttpStatus.OK.value())
                        .message("Delete Wizard Success")
                        .data(null)
                        .build();

    }
    @PutMapping("/{wizardId}/artifacts/{artifactId}")
    public Response<?> assignArtifact (
            @PathVariable("wizardId") int wizardId,
            @PathVariable("artifactId") int artifactId
    ) {
        wizardService.assignArtifact(wizardId, artifactId);
        return Response
                .builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Assign Artifact Success")
                .data(null)
                .build();

    }


}

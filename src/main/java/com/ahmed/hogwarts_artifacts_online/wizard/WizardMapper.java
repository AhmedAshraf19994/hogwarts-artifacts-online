package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WizardMapper {

    public WizardResponseDto toWizardResponseDto (Wizard wizard) {
        return new WizardResponseDto(
                wizard.getId(),
                wizard.getName(),
                wizard.getArtifacts() == null ? 0 : wizard.getNumberOfArtifacts()
        );

    }

    public Wizard toWizard(CreateWizardDto createWizardDto) {
        return Wizard
                .builder()
                .name(createWizardDto.name())
                .build();
    }
}

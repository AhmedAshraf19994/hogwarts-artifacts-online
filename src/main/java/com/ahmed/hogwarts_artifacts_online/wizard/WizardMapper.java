package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
import org.springframework.stereotype.Service;

@Service
public class WizardMapper {

    public WizardResponseDto toWizardResponseDto (Wizard wizard) {
        return new WizardResponseDto(
                wizard.getId(),
                wizard.getName(),
                wizard.getNumberOfArtifacts()
        );

    }
}

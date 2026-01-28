package com.ahmed.hogwarts_artifacts_online.artifact.dto;

import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;

public record ArtifactResponseDto (
        int id,
        String name,
        String description,
        String imageUrl,
        WizardResponseDto wizardResponseDto
){
}

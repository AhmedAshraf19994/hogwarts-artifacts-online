package com.ahmed.hogwarts_artifacts_online.wizard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateWizardDto (
        @NotEmpty(message = "name is required")
        @NotBlank
        String name
) {
}

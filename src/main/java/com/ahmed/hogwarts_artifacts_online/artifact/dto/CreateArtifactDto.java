package com.ahmed.hogwarts_artifacts_online.artifact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateArtifactDto (

        @NotEmpty(message = "name is required")
        @NotBlank
        String name,
        @NotEmpty(message = "name is required")
        @NotBlank
        String description,
        @NotEmpty(message = "name is required")
        @NotBlank
        String imageUrl

){
}

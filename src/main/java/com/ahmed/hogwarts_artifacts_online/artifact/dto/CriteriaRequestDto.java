package com.ahmed.hogwarts_artifacts_online.artifact.dto;

public record CriteriaRequestDto(
        Integer id,
        String name,
        String description,
        String wizardName
) {
}

package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.wizard.WizardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtifactMapper {
    private final WizardMapper wizardMapper;

    public ArtifactResponseDto toArtifactResponseDto (Artifact artifact) {
        if (artifact.getWizard() == null) {
            return new ArtifactResponseDto(
                    artifact.getId(),
                    artifact.getName(),
                    artifact.getDescription(),
                    artifact.getImageUrl(),
                    null
            );
        }
         return new ArtifactResponseDto(
                artifact.getId(),
                artifact.getName(),
                artifact.getDescription(),
                artifact.getImageUrl(),
                wizardMapper.toWizardResponseDto(artifact.getWizard())
        );

    }
}

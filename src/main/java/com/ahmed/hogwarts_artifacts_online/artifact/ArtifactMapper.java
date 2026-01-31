package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.wizard.WizardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtifactMapper {

    private final WizardMapper wizardMapper;

    public ArtifactResponseDto toArtifactResponseDto (Artifact artifact) {

            return new ArtifactResponseDto(
                    artifact.getId(),
                    artifact.getName(),
                    artifact.getDescription(),
                    artifact.getImageUrl(),
                    artifact.getWizard() == null ? null : wizardMapper.toWizardResponseDto(artifact.getWizard())
            );

    }

    public Artifact toArtifact(CreateArtifactDto createArtifactDto) {
        return  Artifact.builder()
                .name(createArtifactDto.name())
                .description(createArtifactDto.description())
                .imageUrl(createArtifactDto.imageUrl())
                .build();

    }
}

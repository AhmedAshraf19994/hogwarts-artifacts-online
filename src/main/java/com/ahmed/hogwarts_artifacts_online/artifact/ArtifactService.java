package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public ArtifactResponseDto findArtifactById (int id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() ->  new ArtifactNotFoundException(id));
        return artifactMapper.toArtifactResponseDto(artifact);
    }

}

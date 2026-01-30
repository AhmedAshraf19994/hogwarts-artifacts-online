package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public ArtifactResponseDto findArtifactById (int id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() ->  new ArtifactNotFoundException(id));
        return artifactMapper.toArtifactResponseDto(artifact);
    }

    public List<ArtifactResponseDto> findAllArtifacts() {
        List<Artifact> artifacts = artifactRepository.findAll();
        return artifacts .stream()
                .map(artifactMapper::toArtifactResponseDto)
                .collect(Collectors.toList());

    }

    public ArtifactResponseDto saveArtifact(CreateArtifactDto createArtifactDto) {
        Artifact artifact = artifactMapper.toArtifact(createArtifactDto);
        Artifact savedArtifact = artifactRepository.save(artifact);
        return artifactMapper.toArtifactResponseDto(savedArtifact);
    }

    public ArtifactResponseDto updateArtifact(int artifactId, CreateArtifactDto createArtifactDto) {
        return artifactRepository.findById(artifactId).map(artifact -> {
            artifact.setName(createArtifactDto.name());
            artifact.setDescription(createArtifactDto.description());
            artifact.setImageUrl(createArtifactDto.imageUrl());
            Artifact updatedArtifact = artifactRepository.save(artifact);
            return artifactMapper.toArtifactResponseDto(updatedArtifact);
        }).orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public void deleteArtifact (int artifactId) {
         Artifact artifact = artifactRepository.findById(artifactId).
        orElseThrow(() -> new ArtifactNotFoundException(artifactId));
         artifactRepository.deleteById(artifact.getId());

    }
}




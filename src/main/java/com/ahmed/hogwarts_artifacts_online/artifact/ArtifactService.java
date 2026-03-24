package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CriteriaRequestDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.PageResponseDto;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ArtifactMapper artifactMapper;

    public ArtifactResponseDto findArtifactById (int id) {
        Artifact artifact = artifactRepository.findById(id)
                .orElseThrow(() ->  new ObjectNotFoundException("artifact", id));
        return artifactMapper.toArtifactResponseDto(artifact);
    }

    public PageResponseDto<ArtifactResponseDto> findAllArtifacts(Pageable pageable) {

        //to prevent unlimited page size
        if (pageable.getPageSize() > 50) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    50,
                    pageable.getSort()
            );
        }
        Page<Artifact> pageOfArtifacts = artifactRepository.findAll(pageable);
        return artifactMapper.toPageResponseDto(pageOfArtifacts);

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
        }).orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    }

    public void deleteArtifact (int artifactId) {
         Artifact artifact = artifactRepository.findById(artifactId).
        orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
         artifactRepository.deleteById(artifact.getId());

    }

    public PageResponseDto<ArtifactResponseDto> findByCriteria (CriteriaRequestDto searchCriteria, Pageable pageable) {
        // start with empty query
        Specification<Artifact> spec = Specification.unrestricted();

        if(searchCriteria.id() != null) {
            spec = spec.and(ArtifactSpecs.hasId(searchCriteria.id()));
        }

        if(StringUtils.hasLength(searchCriteria.name())) {
            spec = spec.and(ArtifactSpecs.containsName(searchCriteria.name()));
        }

        if(StringUtils.hasLength(searchCriteria.description())) {
            spec = spec.and(ArtifactSpecs.containsDescription(searchCriteria.description()));
        }

        if(StringUtils.hasLength(searchCriteria.wizardName())) {
            spec = spec.and(ArtifactSpecs.hasWizard(searchCriteria.wizardName()));
        }

        Page<Artifact> pageOfArtifacts = artifactRepository.findAll(spec, pageable);

        return artifactMapper.toPageResponseDto(pageOfArtifacts) ;

    };
}




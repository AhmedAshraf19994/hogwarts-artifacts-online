package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.artifact.Artifact;
import com.ahmed.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.ahmed.hogwarts_artifacts_online.system.exceptions.ObjectNotFoundException;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.CreateWizardDto;
import com.ahmed.hogwarts_artifacts_online.wizard.dto.WizardResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;
    private final WizardMapper wizardMapper;
    private final ArtifactRepository artifactRepository;

    public WizardResponseDto  findWizardById (int wizardId) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow( () -> new ObjectNotFoundException("wizard", wizardId));
     return wizardMapper.toWizardResponseDto(wizard);
    }

    public List<WizardResponseDto> findAllWizards () {
        return wizardRepository.findAll().stream().map(wizardMapper::toWizardResponseDto).collect(Collectors.toList());
    }

    public WizardResponseDto saveWizard (CreateWizardDto createWizardDto) {
         Wizard wizard = wizardMapper.toWizard(createWizardDto);
         Wizard savedWizard = wizardRepository.save(wizard);
         return wizardMapper.toWizardResponseDto(savedWizard);
    }

    public WizardResponseDto updateWizard (
            int wizardId,
            CreateWizardDto createWizardDto
    ) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow( () -> new ObjectNotFoundException("wizard" ,wizardId));
                 wizard.setName(createWizardDto.name());
                return wizardMapper.toWizardResponseDto(wizard);
    }

    public void deleteWizard (int wizardId) {
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
        wizard.removeAllArtifacts();
        wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(int wizardId, int artifactId) {
        // find the wizard
        Wizard wizard = wizardRepository.findById(wizardId)
                .orElseThrow( () -> new ObjectNotFoundException("wizard", wizardId));
        //find the artifact
        Artifact artifact = artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        // assign the artifact to new wizard
        // check if the artifact already owned by another wizard
        if (artifact.getWizard() != null) {
            artifact.getWizard().removeArtifact(artifact);
            wizard.addArtifact(artifact);
        } else {
            wizard.addArtifact(artifact);

        }


    }




}

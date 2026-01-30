package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArtifactController {

    private final ArtifactService artifactService;

    @GetMapping("/api/v1/artifacts/{artifactId}")
    public Response<ArtifactResponseDto> findArtifactById (
            @PathVariable ("artifactId") int artifactId
    ) {
        ArtifactResponseDto artifactResponseDto = artifactService.findArtifactById(artifactId);
        return Response
                .<ArtifactResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find Artifact Success")
                .data(artifactResponseDto)
                .build();
    }

    @GetMapping("/api/v1/artifacts")
    public Response<List<ArtifactResponseDto>> test () {
        List<ArtifactResponseDto> artifacts = artifactService.findAllArtifacts();

        return Response
                .<List<ArtifactResponseDto>>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find All Artifacts Success")
                .data(artifacts)
                .build();
    }
    @PostMapping("/api/v1/artifacts")
    public Response<ArtifactResponseDto> saveArtifact (
            @RequestBody @Valid CreateArtifactDto createArtifactDto
            ) {
        ArtifactResponseDto artifactResponseDto = artifactService.saveArtifact(createArtifactDto);
        return Response
                .<ArtifactResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Save Artifact Success")
                .data(artifactResponseDto)
                .build();
    }
    @PutMapping("/api/v1/artifacts/{artifactId}")
    public Response<ArtifactResponseDto> updateArtifact (
            @PathVariable("artifactId") int artifactId,
            @Valid @RequestBody  CreateArtifactDto updateArtifactDto

    ) {
        ArtifactResponseDto artifactResponseDto = artifactService.updateArtifact(artifactId, updateArtifactDto);
        return Response
                .<ArtifactResponseDto>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Update Artifact Success")
                .data(artifactResponseDto)
                .build() ;

    }
}

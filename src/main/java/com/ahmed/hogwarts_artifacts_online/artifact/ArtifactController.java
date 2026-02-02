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
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;
    @GetMapping("/{artifactId}")
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

    @GetMapping("")
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

    @PostMapping("")
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

    @PutMapping("/{artifactId}")
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

    @DeleteMapping("/{artifactId}")
    public Response<?> deleteArtifact (@PathVariable("artifactId") int artifactId) {
        artifactService.deleteArtifact(artifactId);
        return Response
                .builder()
                .flag(true)
                .message("Delete Artifact Success")
                .code(HttpStatus.OK.value())
                .data(null)
                .build();
    }
}

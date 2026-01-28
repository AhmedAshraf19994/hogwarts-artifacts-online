package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/")
    public String test () {
        return "server is working";
    }
}

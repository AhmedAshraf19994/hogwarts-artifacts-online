package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.artifact.dto.ArtifactResponseDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CreateArtifactDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.CriteriaRequestDto;
import com.ahmed.hogwarts_artifacts_online.artifact.dto.PageResponseDto;
import com.ahmed.hogwarts_artifacts_online.client.imageStorage.ImageStorageClient;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

    private final ArtifactService artifactService;

//    private final ImageStorageClient imageStorageClient;

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
    public Response<PageResponseDto<ArtifactResponseDto>> findAllArtifacts (Pageable pageable) {
        PageResponseDto<ArtifactResponseDto> pageOfArtifactsResponseDto = artifactService.findAllArtifacts(pageable);

        return Response
                .<PageResponseDto<ArtifactResponseDto>>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message("Find All Artifacts Success")
                .data(pageOfArtifactsResponseDto)
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

    @PostMapping("/search")
    public Response<PageResponseDto<ArtifactResponseDto>> findBySearchCriteria (
            @RequestBody CriteriaRequestDto criteriaRequestDto,
            Pageable pageable
    ) {
        PageResponseDto<ArtifactResponseDto> pageOfArtifactResponseDto= artifactService.findByCriteria(criteriaRequestDto, pageable);

        return Response
                .<PageResponseDto<ArtifactResponseDto>>builder()
                .flag(true)
                .code(HttpStatus.OK.value())
                .message(pageOfArtifactResponseDto.content().isEmpty() ? "No Found Artifacts" : "Search Success")
                .data(pageOfArtifactResponseDto)
                .build();
    }

//    @PostMapping("/images")
//    public Response<String> uploadArtifactImage (
//            @RequestParam String containerName,
//            @RequestParam MultipartFile file
//            ) throws IOException {
//        try (InputStream inputStream = file.getInputStream()) {
//            String imageUrl = imageStorageClient.uploadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
//            return Response
//                    .<String>builder()
//                    .flag(true)
//                    .code(HttpStatus.OK.value())
//                    .message("Upload Image Success")
//                    .data(imageUrl)
//                    .build();
//        }

//    }

}

package com.ahmed.hogwarts_artifacts_online.artifact.dto;

import java.util.List;

public record PageResponseDto<T>(
        List<T> content,
        int page,
        int size,
        Long totalElements,
        int totalPages,
        boolean lastPage,
        boolean firstPage
) {
}

package com.ahmed.hogwarts_artifacts_online.system.exceptions;


import com.ahmed.hogwarts_artifacts_online.artifact.ArtifactNotFoundException;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ArtifactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Response handleArtifactNotFoundException (ArtifactNotFoundException exception) {
    return Response
            .builder()
            .flag(false)
            .code(HttpStatus.NOT_FOUND.value())
            .message(exception.getMessage())
            .data(null)
            .build();
    }
}

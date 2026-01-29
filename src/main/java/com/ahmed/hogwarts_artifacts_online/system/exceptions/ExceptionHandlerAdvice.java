package com.ahmed.hogwarts_artifacts_online.system.exceptions;


import com.ahmed.hogwarts_artifacts_online.artifact.ArtifactNotFoundException;
import com.ahmed.hogwarts_artifacts_online.system.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ArtifactNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Response<?> handleArtifactNotFoundException (ArtifactNotFoundException exception) {
    return Response
            .builder()
            .flag(false)
            .code(HttpStatus.NOT_FOUND.value())
            .message(exception.getMessage())
            .data(null)
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
     Response<?> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {
        //extract the errors from exception
        List<ObjectError> errors= exception.getBindingResult().getAllErrors();
        Map<String,String> returnedErrors = new HashMap<>();
     errors.forEach( err -> {
         String field = ((FieldError) err).getField();
         String errorMessage = err.getDefaultMessage();
         returnedErrors.put(field,errorMessage);
     });
        return Response.
                builder()
                .flag(false)
                .message("provided arguments are not valid, check data for details")
                .code(HttpStatus.BAD_REQUEST.value())
                .data(returnedErrors)
                .build();

    }
}

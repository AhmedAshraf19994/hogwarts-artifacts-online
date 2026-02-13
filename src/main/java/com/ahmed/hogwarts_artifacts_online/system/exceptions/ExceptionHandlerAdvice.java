package com.ahmed.hogwarts_artifacts_online.system.exceptions;


import com.ahmed.hogwarts_artifacts_online.system.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
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

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Response<?> handleObjectNotFoundException (ObjectNotFoundException exception) {
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

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleAuthExceptions (Exception exception) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("username or password is wrong")
                .build();
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleInsufficientAuthException (InsufficientAuthenticationException insufficientAuthenticationException) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("you must log in")
                .data(null)
                .build();
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> handleAccountStatusException (AccountStatusException exception) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("Account is abnormal")
                .data(null)
                .build();
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    Response<?> InvalidBearerTokenException (InvalidBearerTokenException exception) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message("token is invalid")
                .data(null)
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    Response<?> handleAccessDeniedException (AccessDeniedException Exception) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.FORBIDDEN.value())
                .message("No permission")
                .data(null)
                .build();
    }

    /**
    * Fallback to handles any unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    Response<?> handleException (Exception exception) {
        return Response
                .builder()
                .flag(false)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("internal server error occur")
                .data(exception)
                .build();
    }



}

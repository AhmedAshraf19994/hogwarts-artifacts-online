package com.ahmed.hogwarts_artifacts_online.system.exceptions;

public class CustomBlobStorageException extends RuntimeException{

    public CustomBlobStorageException (String message , Exception exception) {
        super(message, exception);
    }

}

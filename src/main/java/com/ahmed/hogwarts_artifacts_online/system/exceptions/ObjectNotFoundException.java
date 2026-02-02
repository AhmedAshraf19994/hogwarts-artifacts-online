package com.ahmed.hogwarts_artifacts_online.system.exceptions;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException (String objectName, int id) {
        super("could not find " + objectName + " with id " + id );
    }
}

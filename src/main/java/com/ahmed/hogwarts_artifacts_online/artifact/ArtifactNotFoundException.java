package com.ahmed.hogwarts_artifacts_online.artifact;

public class ArtifactNotFoundException extends RuntimeException{
    public ArtifactNotFoundException(int id) {
        super("could not find artifact with id " + id);
    }
}

package com.ahmed.hogwarts_artifacts_online.client.imageStorage;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageClient {

    String uploadImage (String containerName, String originalImageName, InputStream data, Long length) throws IOException;
}

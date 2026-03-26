package com.ahmed.hogwarts_artifacts_online.client.imageStorage;

import com.ahmed.hogwarts_artifacts_online.system.exceptions.CustomBlobStorageException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
//@Service
public class AzureImageStorageClient implements ImageStorageClient{

    private final BlobServiceClient blobServiceClient;

    @Override
    public String uploadImage(String containerName, String originalImageName, InputStream data, Long length) throws IOException {
       try {
           // get blobContainerClient instance to interact with a blob container
           BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

           // generate a new unique name for the image
           String newImageName = UUID.randomUUID().toString() + originalImageName.substring(originalImageName.lastIndexOf("."));

           //get blobClient to interact with specified blob
           BlobClient blobClient = blobContainerClient.getBlobClient(newImageName);

           //upload the image to the blob
           blobClient.upload(data, length, true);

           return blobClient.getBlobName();

       } catch (BlobStorageException exception) {
           throw  new CustomBlobStorageException("Failed to upload image to Azure blob storage", exception);
       }
    }
}

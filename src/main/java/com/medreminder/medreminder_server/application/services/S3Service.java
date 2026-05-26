package com.medreminder.medreminder_server.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${yc.s3.bucket}")
    private String bucket;
    @Value("${yc.s3.private.endpoint}")
    private String endpoint;
    private final S3Client client;

    public S3Service(S3Client s3Client) {
        this.client = s3Client;
    }

    public String uploadFile(MultipartFile file) {
        String key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return endpoint + "/" + bucket + "/" + key;

        } catch (IOException e){
            System.err.println("Error closing input stream: " + e.getMessage());
            return null;
        }
    };

    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            client.deleteObject(deleteObjectRequest);
        } catch (Exception e){
            System.err.println("Error closing input stream: " + e.getMessage());
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        URI url = URI.create(fileUrl);
        String path = url.getPath();
        String bucketPrefix = "/" + bucket + "/";
        return path.substring(bucketPrefix.length());
    }
}
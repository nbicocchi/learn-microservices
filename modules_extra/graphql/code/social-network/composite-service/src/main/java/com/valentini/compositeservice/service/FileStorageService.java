package com.valentini.compositeservice.service;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadFile(String folder, MultipartFile file) throws Exception {
        String fileName = generateUniqueFileName(folder, file.getOriginalFilename());

        // Ensure the bucket exists
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        // Upload the file
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return fileName;
    }

    public InputStream getFile(String fileName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public String getContentType(String fileName) throws Exception {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
        return stat.contentType();
    }

    private String generateUniqueFileName(String folder, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return folder + "/" + UUID.randomUUID().toString() + extension;
    }
}


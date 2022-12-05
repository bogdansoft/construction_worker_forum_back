package com.construction_worker_forum_back.service;

import java.io.*;
import java.nio.file.*;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            System.out.println("Error converting multipartFile to file "+ e);
        }
        return convertedFile;
    }

    public static byte[] getAvatarBytes(AmazonS3Client s3Client, String bucketName, String avatar) {
        S3Object object = s3Client.getObject(bucketName, avatar);
        try {
            return IOUtils.toByteArray(object.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

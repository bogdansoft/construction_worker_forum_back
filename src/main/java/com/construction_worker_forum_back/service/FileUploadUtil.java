package com.construction_worker_forum_back.service;

import java.io.*;

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
}

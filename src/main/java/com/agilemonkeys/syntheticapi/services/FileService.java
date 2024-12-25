package com.agilemonkeys.syntheticapi.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileService {
    public static final String IMAGE_DIR = "upload/";

    public String saveImage(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path imagePath = Paths.get(IMAGE_DIR, fileName);
        Files.createDirectories(imagePath.getParent());
        Files.write(imagePath, file.getBytes());
        return fileName;
    }
}

package com.javatechie.service;

import com.javatechie.entity.FileDetails;
import com.javatechie.entity.ImageData;
import com.javatechie.respository.FileDetailsRepository;
import com.javatechie.respository.StorageRepository;
import com.javatechie.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;
    @Autowired
    private FileDetailsRepository fileDetailsRepository;

    private final String FOLDER = "/Users/javatechie/Desktop/MyFiles/";

    public String uploadImage(MultipartFile file) throws IOException {

        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }

    public String uploadImageToFolder(MultipartFile file) throws IOException {
        String filePath = FOLDER + file.getOriginalFilename();
        FileDetails fileDetails = fileDetailsRepository.save(FileDetails.builder().
                name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());
        file.transferTo(new File(filePath));
        if (fileDetails != null) {
            return "file uploaded successfully : " + filePath;
        }
        return null;
    }


    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }

    public byte[] downloadImageFromFolder(String fileName) throws IOException {
        Optional<FileDetails> fileDetails = fileDetailsRepository.findByName(fileName);
        String filePath=fileDetails.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }
}

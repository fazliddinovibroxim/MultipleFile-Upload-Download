package com.example.githup_files.serverFiles;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadController {

    private final static String uploadDirectory = "src/main/resources/uploadDirectory/";

    @Autowired
    FileRepository fileRepository;

    @PostMapping("/uploadMultipleFiles")
    public List<FileEntity> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<FileEntity> savedFiles = new ArrayList<>();
        if (files.length == 0)
            return null;
        for (MultipartFile file : files) {
            String serverName = UUID.randomUUID().toString().replaceAll("-", "")
                                .concat(file.getOriginalFilename()
                                 .substring(file.getOriginalFilename().indexOf('.')));
            FileEntity fileEntity = new FileEntity(null, file.getOriginalFilename(), serverName, file.getContentType(), file.getSize());
            try {
                file.transferTo(Path.of(uploadDirectory + serverName));
                savedFiles.add(fileEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileRepository.saveAll(savedFiles);
    }
}

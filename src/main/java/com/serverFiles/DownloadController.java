package com.serverFiles;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class DownloadController {

    private final static String uploadDirectory = "src/main/resources/uploadDirectory/";

    @Autowired
    FileRepository fileRepository;

    @GetMapping("/downloadMultipleFiles")
    public ResponseEntity<InputStreamResource> downloadMultipleFiles(@RequestBody String[] fileNames) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        for (String fileName : fileNames) {
            File file = new File(uploadDirectory + fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
        byteArrayOutputStream.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=files.zip");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(byteArrayOutputStream.toByteArray().length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}

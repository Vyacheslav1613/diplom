package ru.netology.diplom.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.Jwt.JwtUtil;
import ru.netology.diplom.Model.FileEntity;
import ru.netology.diplom.Model.FileResponse;
import ru.netology.diplom.Service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {
    private final FileService fileService;
    private String username;
    private final JwtUtil jwtUtil;

    @Autowired
    public FileController(FileService fileService, JwtUtil jwtUtil) {
        this.fileService = fileService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestParam("file") MultipartFile file) {

        try {
            FileEntity fileEntity = fileService.saveFile(username, filename, file);
            return ResponseEntity.ok(fileEntity);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reading file data: " + e.getMessage());
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename) {
        FileEntity fileEntity = fileService.findByFilename(filename);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }
        fileService.deleteFile(fileEntity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/file")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("filename") String filename) throws IOException {
        FileEntity fileEntity = fileService.findByFilename(filename);
        if (fileEntity == null) {
            throw new RuntimeException("Файл не найден");
        }

        ByteArrayResource resource = new ByteArrayResource(fileEntity.getFileData());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileEntity.getFileData().length)
                .body(resource);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> listFiles(@RequestHeader("auth-token") String authToken) {
        isValidToken(authToken);
        List<FileEntity> fileEntities = fileService.findAllByUsername(username);
        List<FileResponse> files = fileEntities.stream()
                .map(entity -> new FileResponse(entity.getFilename(), entity.getEditedAt(), entity.getSize(),
                        entity.getIsError(), entity.getIsSelected(), entity.getExtension()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(files);
    }

    private boolean isValidToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        username = jwtUtil.extractUsername(authToken);
        return true;
    }
}


package ru.netology.diplom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.Error.ErrorLoginOrPassword;
import ru.netology.diplom.Model.FileEntity;
import ru.netology.diplom.Model.FileResponse;
import ru.netology.diplom.Jwt.JwtRequestFilter;
import ru.netology.diplom.Jwt.JwtUtil;
import ru.netology.diplom.Model.Users;
import ru.netology.diplom.Repository.FileRepository;
import ru.netology.diplom.Request.LoginRequest;
import ru.netology.diplom.Service.UserService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AuthController {
    public String username;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final FileRepository fileRepository;

    @Value("${server.port}")
    public int port;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, FileRepository fileRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.fileRepository = fileRepository;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.ok(Map.of(
                "message", "welcome"
        ));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("")));
        }

        System.out.println(port);
        String storedEncodedPassword = getEncodedPasswordFromDatabase(loginRequest.getLogin());
        if (storedEncodedPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("Если у вас нет учетной записи," +
                            "попробуйте зарегистрироваться по адресу " +
                            "localhost:" + port + "/register")));
        }

        if (userService.checkUserPassword(loginRequest.getLogin(), loginRequest.getPassword())) {
            String token = jwtUtil.generateToken(loginRequest.getLogin());
            return ResponseEntity.ok(Map.of(
                    "auth-token", token,
                    "email", loginRequest.getLogin(),
                    "message", "Успешная авторизация!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("")));
        }
    }


    private String getEncodedPasswordFromDatabase(String email) {
        Users user = userService.getUserByEmail(email);
        return user != null ? user.getPassword() : null;
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout2(@RequestHeader("auth-token") String authToken) {
        System.out.println("GET");
        return ResponseEntity.ok("GET logout successful");
    }


    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestHeader("auth-token") String authToken,
                                                          @RequestParam("filename") String filename,
                                                          @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        response.put("filename", filename);
        response.put("editedAt", System.currentTimeMillis());
        response.put("size", file.getSize());
        response.put("error", false);
        response.put("selected", false);
        response.put("extension", FilenameUtils.getExtension(file.getOriginalFilename()));

        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            response.put("error", true);
            response.put("message", "Error reading file data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }


        FileEntity fileEntity = new FileEntity();
        fileEntity.setUsername(username);
        fileEntity.setFilename(filename);
        fileEntity.setEditedAt(System.currentTimeMillis());
        fileEntity.setSize(file.getSize());
        fileEntity.setIsError(false);
        fileEntity.setIsSelected(false);
        fileEntity.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        fileEntity.setFileData(fileData);


        try {
            fileRepository.save(fileEntity);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Database error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestHeader("auth-token") String authToken,
                                           @RequestParam("filename") String filename) {
        FileEntity fileEntity = fileRepository.findByFilename(filename);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        fileRepository.delete(fileEntity);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/file")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename) throws IOException {
        FileEntity fileEntity = fileRepository.findByFilename(filename);
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

    @PutMapping("/file")
    public ResponseEntity<Void> updateFilename(@RequestParam String filename,
                                               @RequestBody String newFilenameJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMap = mapper.readValue(newFilenameJson, Map.class);
        String newFilename = jsonMap.get("filename");

        FileEntity fileEntity = fileRepository.findByFilename(filename);
        if (fileEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        fileEntity.setFilename(newFilename);
        fileRepository.save(fileEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> listFiles(@RequestHeader("auth-token") String authToken) {
        isValidToken(authToken);

        List<FileEntity> fileEntities = fileRepository.findAllByUsername(username);

        List<FileResponse> files = fileEntities.stream()
                .map(entity -> new FileResponse(entity.getFilename(), entity.getEditedAt(), entity.getSize(),
                        entity.getIsError(), entity.getIsSelected(), entity.getExtension()))
                .collect(Collectors.toList());

        if (files.isEmpty()) {
            System.out.println("No records found for user: " + username);
        } else {
            System.out.println("Found " + files.size() + " files for user: " + username);
        }

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
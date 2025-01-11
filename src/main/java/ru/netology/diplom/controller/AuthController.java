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
import ru.netology.diplom.Error.Data;
import ru.netology.diplom.Repository.FileDatBase;
import ru.netology.diplom.Model.FileResponse;
import ru.netology.diplom.Jwt.JwtRequestFilter;
import ru.netology.diplom.Jwt.JwtUtil;
import ru.netology.diplom.Request.LoginRequest;
import ru.netology.diplom.Service.UserService;

import java.io.IOException;
import java.util.*;

@RestController
public class AuthController {
    public String username;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final FileDatBase fileDatBase;

    @Value("${server.port}")
    public int port;


    @Autowired
    public AuthController(UserService userService, JdbcTemplate jdbcTemplate, JwtRequestFilter jwtRequestFilter, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jdbcTemplate = jdbcTemplate;
        this.jwtUtil = jwtUtil;
        this.fileDatBase = new FileDatBase(jdbcTemplate);
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.ok(Map.of(
                "message", "welcome"
        ));
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        if (loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Data(List.of("Ошибка логина или пароля"), List.of("")));
        }

        System.out.println(port);
        String storedEncodedPassword = getEncodedPasswordFromDatabase(loginRequest.getLogin());
        if (storedEncodedPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Data(List.of("Ошибка логина или пароля"), List.of("Если у вас нет учетной записи," +
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
                    .body(new Data(List.of("Ошибка логина или пароля"), List.of("")));
        }
    }



    private String getEncodedPasswordFromDatabase(String email) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT password FROM netology.users WHERE email = :email";
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Пользователь не найден: " + email);
            return null;
        } catch (Exception e) {
            System.out.println("Ошибка при получении пароля: " + e.getMessage());
            return null;
        }
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

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> response = new HashMap<>();
        response.put("filename", filename);
        response.put("editedAt", System.currentTimeMillis()); // Время редактирования
        response.put("size", file.getSize());
        response.put("error", false);
        response.put("selected", false);
        response.put("extension", FilenameUtils.getExtension(file.getOriginalFilename())); // Получение расширения

        // Преобразование файла в массив байтов
        byte[] fileData;
        try {
            fileData = file.getBytes(); // Получение содержимого файла
        } catch (IOException e) {
            response.put("error", true);
            response.put("message", "Error reading file data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        String sql = "INSERT INTO netology.admin (filename, edited_at, size, is_error, is_selected, extension, file_data) " +
                "VALUES (:filename, :editedAt, :size, :error, :selected, :extension, :fileData)";

        response.put("fileData", fileData); // Добавление содержимого файла в ответ

        try {
            namedParameterJdbcTemplate.update(sql, response);
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
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);


        // Параметризованный SQL-запрос для удаления записи
        String sql = "DELETE FROM netology." + username + " WHERE filename = :filename";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("filename", filename);

        int rowsDeleted = namedParameterJdbcTemplate.update(sql, parameters);

        if (rowsDeleted == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build(); // Успешное удаление
    }


    @GetMapping("/file")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestHeader("auth-token") String authToken,
            @RequestParam("filename") String filename) throws IOException {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        Map<String, Object> params = new HashMap<>();
        params.put("filename", filename);

        byte[] fileData = namedParameterJdbcTemplate.queryForObject(
                "SELECT file_data FROM netology." + username + " WHERE filename = :filename",
                params,
                byte[].class
        );

        if (fileData == null || fileData.length == 0) {
            throw new RuntimeException("Файл не найден");
        }

        ByteArrayResource resource = new ByteArrayResource(fileData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", filename); // Скачивание файла
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileData.length)
                .body(resource);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> updateFilename(@RequestParam String filename,
                                               @RequestBody String newFilenameJson) throws JsonProcessingException {

        // Используем Jackson для парсинга JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> jsonMap = mapper.readValue(newFilenameJson, Map.class);

        // Извлекаем новое имя файла из карты
        String newFilename = jsonMap.get("filename");

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("oldFilename", filename);
        params.put("newFilename", newFilename);

        int rowsUpdated = namedParameterJdbcTemplate.update(
                "UPDATE netology." + username + " SET filename = :newFilename WHERE filename = :oldFilename",
                params
        );

        if (rowsUpdated > 0) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//
    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> listFiles(@RequestHeader("auth-token") String authToken) {
        isValidToken(authToken);

        List<FileResponse> files = getFilesFromDatabase();
        return ResponseEntity.ok(files);
    }

    private List<FileResponse> getFilesFromDatabase() {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String tableName = "netology." + username.toLowerCase(); // Форматируем имя таблицы

        // Параметризованный SQL-запрос
        String sql = "SELECT filename, edited_at, size, extension "
                + "FROM netology." + username;

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", tableName);

        List<FileResponse> files = namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            String filename = rs.getString("filename");
            long editedAt = rs.getLong("edited_at");
            long size = rs.getLong("size");
            String ext = rs.getString("extension");

            // Создаем объект FileResponse с правильными значениями
            return new FileResponse(filename, editedAt, size, false, false, ext);
        });

        if (files.isEmpty()) {
            System.out.println("No records found for user: " + username);
        } else {
            System.out.println("Found " + files.size() + " files for user: " + username);
        }

        return files;
    }



        private boolean isValidToken(String authToken) {
        if (username == null) {
            if (authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            username = jwtUtil.extractUsername(authToken);
            int atIndex = username.indexOf('@');
            if (atIndex != -1) {
                username = username.substring(0, atIndex); // Берем все символы от начала до позиции перед символом '@'
            }
        }
    return true;
    }
}
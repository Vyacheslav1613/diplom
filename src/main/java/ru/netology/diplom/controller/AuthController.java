package ru.netology.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.diplom.Error.ErrorLoginOrPassword;
import ru.netology.diplom.Request.LoginRequest;
import ru.netology.diplom.Service.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class AuthController {
    private final UserService userService;

    @Value("${server.port}")
    public int port;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.ok(Map.of("message", "welcome"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("")));
        }

        String storedEncodedPassword = userService.getUserByEmail(loginRequest.getLogin()).getPassword();
        if (storedEncodedPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("Если у вас нет учетной записи, попробуйте зарегистрироваться по адресу localhost:" + port + "/register")));
        }

        if (userService.checkUserPassword(loginRequest.getLogin(), loginRequest.getPassword())) {
            String token = userService.generateToken(loginRequest.getLogin());
            return ResponseEntity.ok(Map.of(
                    "auth-token", token,
                    "email", loginRequest.getLogin(),
                    "message", "Успешная авторизация!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorLoginOrPassword(List.of("Ошибка логина или пароля"), List.of("")));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        return ResponseEntity.ok("Logged out successfully");
    }
}

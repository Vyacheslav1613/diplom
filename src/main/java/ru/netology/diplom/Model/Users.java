package ru.netology.diplom.Model;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(schema = "netology", name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Автоматически увеличиваемый идентификатор

    @Column(nullable = false)
    private String password; // Пароль (хранится в зашифрованном виде)

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Электронная почта

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt; // Дата и время создания записи

    // Конструктор
    public Users() {}

    public Users(String email, String password) {
        this.email = email;
        this.password = encodePassword(password); // Кодируем пароль при создании
        this.createdAt = LocalDateTime.now(); // Устанавливаем дату создания
    }

    // Метод для кодирования пароля
    private String encodePassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    // Метод для проверки пароля
    public boolean checkPassword(String rawPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, this.password);
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Не добавляем геттер для пароля
}

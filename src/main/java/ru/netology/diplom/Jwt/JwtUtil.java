package ru.netology.diplom.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

    private String SECRET_KEY = "your_secret_key"; // Замените на свой секретный ключ

    // Метод для генерации токена
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Токен действителен 10 часов
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Метод для проверки токена
    public Boolean validateToken(String token, String email) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(email) && !isTokenExpired(token));
    }

    // Метод для извлечения имени пользователя из токена
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Метод для проверки срока действия токена
    public Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Метод для извлечения всех заявлений из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}


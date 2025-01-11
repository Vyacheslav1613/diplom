package ru.netology.diplom.reg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
//import ru.netology.diplom.Repository.FileDatBase;

@Service
public class UsersService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password) {
//        FileDatBase fileDatBase = new FileDatBase(jdbcTemplate);
        String encodedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO netology.users (password, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, encodedPassword, email);
        email = email.split("@")[0];
        String sql1 = "CREATE TABLE if not exists netology." + email + " ( " +
                "id SERIAL PRIMARY KEY, " +
                "filename VARCHAR(255) NOT NULL, " +
                "edited_at BIGINT NOT NULL, " +
                "size BIGINT NOT NULL, " +
                "is_error BOOLEAN NOT NULL, " +
                "is_selected BOOLEAN NOT NULL, " +
                "extension VARCHAR(10) NOT NULL, " +
                "file_data BYTEA);";
        try {
            jdbcTemplate.execute(sql1);
            System.out.println("Таблица создана");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
}
package ru.netology.diplom.reg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsServ {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsServ(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO netology.users (password, email) VALUES (?, ?)";
        jdbcTemplate.update(sql, encodedPassword, email);
    }
}
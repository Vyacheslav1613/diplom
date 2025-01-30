package ru.netology.diplom.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.netology.diplom.Jwt.JwtUtil;
import ru.netology.diplom.Model.Users;
import ru.netology.diplom.Repository.UsersRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UsersRepository usersRepository, JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public static class User {
        private int id;
        private String password;
        private String email;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "User {" + "id=" + id + ", password='" + password + '\'' + ", email='" + email + '\'' + '}';
        }
    }

    public boolean checkUserPassword(String email, String rawPassword) {
        String storedEncodedPassword = getEncodedPasswordFromDatabase(email);
        if (storedEncodedPassword == null) {

            return false;
        }
        return passwordEncoder.matches(rawPassword, storedEncodedPassword);
    }

    private String getEncodedPasswordFromDatabase(String email) {
        String sql = "SELECT password FROM netology.users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, String.class);
        } catch (Exception e) {
            System.out.println("Ошибка при получении пароля для пользователя " + email + ": " + e.getMessage());
            return null;
        }
    }


    public List<User> getAllUsers() {
        String sql = "SELECT id, email, password FROM netology.users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }


    public void updateUserPassword(int userId, String encodedPassword) {
        String sql = "UPDATE netology.users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, encodedPassword, userId);
    }


    public void encodeAndUpdatePasswords() {
        List<User> users = getAllUsers();
        for (User  user : users) {
            String rawPassword = user.getPassword();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            updateUserPassword(user.getId(), encodedPassword);
        }
    }

    public String generateToken(String email) {
        return jwtUtil.generateToken(email);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
}
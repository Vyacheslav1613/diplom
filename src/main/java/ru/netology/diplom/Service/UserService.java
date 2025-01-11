package ru.netology.diplom.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.netology.diplom.Repository.UserRepos;
import ru.netology.diplom.Model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    private final UserRepos userRepos; // Добавляем UserRepos
    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepos userRepos, JdbcTemplate jdbcTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.userRepos = userRepos; // Инициализация UserRepos
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    // Метод для получения пользователя по email
    public Users getUserByEmail(String email) {
        return userRepos.findByEmail(email); // Вызов метода findByEmail
    }

    // Остальные методы...


public static class User {
        private int id;
        private String password;
        private String email;

        // Геттеры и сеттеры
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

    // Метод для получения закодированного пароля из базы данных
    private String getEncodedPasswordFromDatabase(String email) {
        String sql = "SELECT password FROM netology.users WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, String.class);
        } catch (Exception e) {
            System.out.println("Ошибка при получении пароля для пользователя " + email + ": " + e.getMessage());
            return null; // Возвращаем null, если произошла ошибка
        }
    }

    // Метод для получения всех пользователей
    public List<User> getAllUsers() {
        String sql = "SELECT id, email, password FROM netology.users"; // Запрос для получения всех пользователей
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    // Метод для обновления пароля пользователя
    public void updateUserPassword(int userId, String encodedPassword) {
        String sql = "UPDATE netology.users SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, encodedPassword, userId);
    }

    // Метод для кодирования и обновления всех паролей
    public void encodeAndUpdatePasswords() {
        List<User> users = getAllUsers();
        for (User  user : users) {
            String rawPassword = user.getPassword(); // Получаем открытый пароль
            String encodedPassword = passwordEncoder.encode(rawPassword); // Кодируем пароль
            updateUserPassword(user.getId(), encodedPassword); // Обновляем пароль в базе данных
        }
    }

    // RowMapper для преобразования результата запроса в объект User
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

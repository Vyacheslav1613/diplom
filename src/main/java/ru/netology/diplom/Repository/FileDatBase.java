package ru.netology.diplom.Repository;

import org.springframework.jdbc.core.JdbcTemplate;

public class FileDatBase {
    private final JdbcTemplate jdbcTemplate;

    public FileDatBase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void dataBaseForUser(String nameDatabase){
        nameDatabase = nameDatabase.split("@")[0];
        String sql = "CREATE TABLE if not exists netology." + nameDatabase + " ( " +
                "id SERIAL PRIMARY KEY, " +
                "filename VARCHAR(255) NOT NULL, " +
                "edited_at BIGINT NOT NULL, " +
                "size BIGINT NOT NULL, " +
                "is_error BOOLEAN NOT NULL, " +
                "is_selected BOOLEAN NOT NULL, " +
                "extension VARCHAR(10) NOT NULL, " +
                "file_data BYTEA);";
        try {
            jdbcTemplate.execute(sql);
            System.out.println("Таблица создана");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
}

create schema netology;

CREATE TABLE netology.users (
    id SERIAL PRIMARY KEY,          -- Автоматически увеличиваемый идентификатор username VARCHAR(50) NOT NULL, -- Имя пользователя, не может быть NULL
    password VARCHAR(255) NOT NULL, -- Пароль, не может быть NULL
    email VARCHAR(100) UNIQUE NOT NULL, -- Электронная почта, уникальная и не может быть NULL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Дата и время создания записи
);

INSERT INTO netology.users (password, email) VALUES ('admin', 'admin@netology.ru');

select * from netology.users;

select * from netology.admin;

drop table netology.admin cascade;

drop schema netology cascade;

SELECT password FROM netology.users WHERE email = 'admin@netology.ru'


DELETE FROM netology.admin WHERE id = 1;



CREATE TABLE if not exists netology.files (
    id SERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    edited_at BIGINT NOT NULL,
    size BIGINT NOT NULL,
    is_error BOOLEAN NOT NULL,
    is_selected BOOLEAN NOT NULL,
    extension VARCHAR(10) NOT NULL,
    file_data BYTEA
);

select id from netology.admin;

drop table netology.admin;

select * from netology.users;

UPDATE netology.admin SET filename = '534.png' WHERE filename = '{"filename":"787.png"}';

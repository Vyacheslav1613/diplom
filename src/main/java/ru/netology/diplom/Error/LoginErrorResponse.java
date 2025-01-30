package ru.netology.diplom.Error;


import java.util.List;

public class LoginErrorResponse extends ErrorLoginOrPassword {
    public LoginErrorResponse(int port) {
        super(List.of("Ошибка логина или пароля"),
                List.of("Если у вас нет учетной записи, попробуйте зарегистрироваться по адресу localhost:" + port + "/register"));
    }
}


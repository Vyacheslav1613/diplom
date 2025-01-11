package ru.netology.diplom.Error;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ErrorLoginOrPassword {
    private List<String> email;
    private List<String> password;

    public ErrorLoginOrPassword(List<String> email, List<String> password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "data:{" +
                "email=" + email +
                ", password=" + password +
                '}';
    }
}

package ru.netology.diplom.Error;

import java.util.List;

public class Data {
    private List<String> email;
    private List<String> password;

    public Data(List<String> email, List<String> password) {
        this.email = email;
        this.password = password;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getPassword() {
        return password;
    }

    public void setPassword(List<String> password) {
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

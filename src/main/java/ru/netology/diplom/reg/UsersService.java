package ru.netology.diplom.reg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.netology.diplom.Model.Users;
import ru.netology.diplom.Repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void registerUser (String email, String password) {
        Users user = new Users(email, password);
        usersRepository.save(user);
    }

    public boolean userExists(String email) {
        return usersRepository.findByEmail(email) != null;
    }
}

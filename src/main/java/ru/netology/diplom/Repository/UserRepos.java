package ru.netology.diplom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplom.Model.Users;

@Repository
public interface UserRepos extends JpaRepository<Users, Long> {
    Users findByEmail(String username);
}


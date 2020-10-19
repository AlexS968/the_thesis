package application.service;

import application.persistence.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    Optional<User> findByCode(String code);

    boolean isUserEmailExist(String email);

    User save(User user);
}

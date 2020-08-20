package application.service.interfaces;

import application.model.User;

import java.util.Optional;

public interface UserService {

    User findUserByCode(String code);

    Optional<User> findUserById(Long id);

    void restorePassword(String email);

    User saveUser(User user);
}

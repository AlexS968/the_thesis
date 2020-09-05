package application.service.interfaces;

import application.api.request.PasswordRestoreRequest;
import application.model.User;

import java.util.Optional;

public interface UserService {

    User findUserByCode(String code);

    Optional<User> findUserById(Long id);

    void restorePassword(PasswordRestoreRequest request);

    User saveUser(User user);
}

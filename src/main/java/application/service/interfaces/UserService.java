package application.service.interfaces;

import application.model.User;

public interface UserService {

    User findUserByCode(String code);

    boolean checkUserByEmail(String email);

    User saveUser(User user);
}

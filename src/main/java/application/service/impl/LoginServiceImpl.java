package application.service.impl;

import application.model.User;
import application.repository.UserRepository;
import application.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    //мапа с идентификаторами сессий
    private final Map<String, Long> sessionsId = new HashMap<>();

    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //проверить/добавить логику в случае модератора
    public User userAuthentication(String email, String password, String sessionId) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                sessionsId.put(sessionId, user.getId());
            } else {
                user = null;
            }
        }
        return user;
    }
}

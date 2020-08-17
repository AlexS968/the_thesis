package application.service;

import application.exception.ApiValidationException;
import application.exception.EntityNotFoundException;
import application.model.User;
import application.repository.UserRepository;
import application.service.interfaces.LoginService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    //sessions id
    private static final Map<String, Long> sessionsId = new HashMap<>();

    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User userAuthentication(String email, String password, String sessionId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiValidationException(""));
        if (user.getPassword().equals(password)) {
            sessionsId.put(sessionId, user.getId());
        } else {
            throw new ApiValidationException("");
        }
        return user;
    }

    public void logout(String sessionId) {
        sessionsId.remove(sessionId);
    }

    public static Map<String, Long> getSessionsId() {
        return sessionsId;
    }
}

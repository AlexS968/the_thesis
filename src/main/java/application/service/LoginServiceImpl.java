package application.service;

import application.api.request.LoginRequest;
import application.model.User;
import application.repository.UserRepository;
import application.service.interfaces.LoginService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    //sessions id
    private static final Map<String, Long> sessionsId = new HashMap<>();

    private final UserRepository userRepository;

    public LoginServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void userAuthentication(LoginRequest request, HttpSession session) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(request.getPassword())) {
                sessionsId.put(session.getId(), user.get().getId());
            }
        }
    }

    public void logout(HttpSession session) {
        sessionsId.remove(session.getId());
    }

    public static Map<String, Long> getSessionsId() {
        return sessionsId;
    }

    public void addSessionId (String sessionId, long userId){
        sessionsId.put(sessionId,userId);
    }
}

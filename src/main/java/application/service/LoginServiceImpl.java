package application.service;

import application.api.request.LoginRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.type.UserAuthCheckResponse;
import application.exception.EntNotFoundException;
import application.model.User;
import application.repository.UserRepository;
import application.service.interfaces.LoginService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PostServiceImpl postService;

    //sessions id
    private static final Map<String, Long> sessionsId = new HashMap<>();

    @Bean
    public ModelMapper authModelMapper() {
        return new ModelMapper();
    }

    @Override
    public void userAuthentication(LoginRequest request, HttpSession session) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            if (user.get().getPassword().equals(request.getPassword())) {
                sessionsId.put(session.getId(), user.get().getId());
            }
        }
    }

    @Override
    public AuthenticationResponse getAuthenticationResponse(HttpSession session) {
        AuthenticationResponse response;
        Long userId = LoginServiceImpl.getSessionsId().get(session.getId());
        if (userId == null) {
            response = new AuthenticationResponse(false);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(EntNotFoundException::new);
            UserAuthCheckResponse userResponse = new UserAuthCheckResponse();
            authModelMapper().map(user, userResponse);
            userResponse.setModerationCount(!user.isModerator() ?
                    0 : postService.getPostsForModeration(session, "new").size());
            response = new AuthenticationResponse(true, userResponse);
        }
        return response;
    }

    @Override
    public void logout(HttpSession session) {
        sessionsId.remove(session.getId());
    }

    @Override
    public void addSessionId(String sessionId, long userId) {
        sessionsId.put(sessionId, userId);
    }

    public static Map<String, Long> getSessionsId() {
        return sessionsId;
    }
}

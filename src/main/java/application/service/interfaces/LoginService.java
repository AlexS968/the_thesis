package application.service.interfaces;

import application.api.request.LoginRequest;
import application.api.response.AuthenticationResponse;
import application.model.User;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public interface LoginService {

    void userAuthentication(LoginRequest request, HttpSession session);

    AuthenticationResponse getAuthenticationResponse(HttpSession session);

    void logout (HttpSession session);

    void addSessionId (String sessionId, long userId);
}

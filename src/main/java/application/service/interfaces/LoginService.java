package application.service.interfaces;

import application.model.User;

import java.util.Map;

public interface LoginService {

    User userAuthentication(String email, String password, String sessionId);

    void logout (String sessionId);
}

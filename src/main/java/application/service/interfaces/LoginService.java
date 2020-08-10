package application.service.interfaces;

import application.model.User;

public interface LoginService {

    User userAuthentication(String email, String password, String sessionId);
}

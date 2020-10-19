package application.service;

import application.api.request.LoginRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.ResultResponse;

import java.security.Principal;

public interface LoginService {

    AuthenticationResponse check(Principal principal);

    AuthenticationResponse login(LoginRequest request);

    ResultResponse logout(Principal principal);
}

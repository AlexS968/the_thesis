package application.service;

import application.api.response.AuthCheckResponse;
import application.service.interfaces.AuthCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthCheckServiceImpl implements AuthCheckService {

    public ResponseEntity<AuthCheckResponse> getAuthCheck() {

        //ставим заглушку - пользователь неавторизован
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();
        authCheckResponse.setResult(false);
        return new ResponseEntity<>(authCheckResponse, HttpStatus.OK);
    }
}

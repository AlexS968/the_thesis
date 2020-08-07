package application.service;

import application.dto.response.AuthCheckResponse;
import org.springframework.http.ResponseEntity;

public interface AuthCheckService {

    ResponseEntity<AuthCheckResponse> getAuthCheck();
}

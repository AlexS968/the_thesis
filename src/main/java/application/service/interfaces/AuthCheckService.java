package application.service.interfaces;

import application.api.response.AuthCheckResponse;
import org.springframework.http.ResponseEntity;

public interface AuthCheckService {

    ResponseEntity<AuthCheckResponse> getAuthCheck();
}

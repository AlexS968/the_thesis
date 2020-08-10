package application.service.interfaces;

import application.api.response.InitResponse;
import org.springframework.http.ResponseEntity;

public interface InitService {

    ResponseEntity<InitResponse> getInit();
}

package application.service;

import application.dto.response.InitResponse;
import org.springframework.http.ResponseEntity;

public interface InitService {

    ResponseEntity<InitResponse> getInit();
}

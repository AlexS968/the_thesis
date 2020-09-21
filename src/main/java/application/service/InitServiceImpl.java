package application.service;

import application.api.response.InitResponse;
import application.service.interfaces.InitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InitServiceImpl implements InitService {

    @Override
    public ResponseEntity<InitResponse> getInit() {
        InitResponse initResponse = new InitResponse();
        initResponse.setTitle("DevPub");
        initResponse.setSubtitle("Program developer stories");
        initResponse.setPhone("+7 903 666-44-55");
        initResponse.setEmail("mail@mail.ru");
        initResponse.setCopyright("Dmitry Sergeev");
        initResponse.setCopyrightFrom("2005");
        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }
}

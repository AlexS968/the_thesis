package application.service.impl;

import application.dto.response.InitResponse;
import application.service.InitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InitServiceImpl implements InitService {

    public ResponseEntity<InitResponse> getInit() {

        InitResponse initResponse = new InitResponse();
        initResponse.setTitle("DevPub");
        initResponse.setSubtitle("Рассказы разработчиков");
        initResponse.setPhone("+7 903 666-44-55");
        initResponse.setEmail("mail@mail.ru");
        initResponse.setCopyright("Дмитрий Сергеев");
        initResponse.setCopyrightFrom("2005");

        return new ResponseEntity<>(initResponse, HttpStatus.OK);
    }
}

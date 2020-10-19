package application.service.mapper;

import application.api.response.CaptchaResponse;
import application.persistence.model.CaptchaCode;
import org.springframework.stereotype.Service;

@Service
public class CaptchaMapper {

    public CaptchaResponse convertToDto(CaptchaCode captcha, String base64) {
        return new CaptchaResponse(captcha.getSecretCode(),
                "data:image/png;base64, " + base64);
    }
}

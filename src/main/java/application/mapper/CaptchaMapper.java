package application.mapper;

import application.api.response.CaptchaResponse;
import application.model.CaptchaCode;
import com.github.cage.YCage;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class CaptchaMapper {

    public CaptchaResponse convertToDto(CaptchaCode captcha) {
        return new CaptchaResponse(
                captcha.getSecretCode(),
                "data:image/png;base64, " +
                        Base64.getEncoder().encodeToString(
                                new YCage().draw(captcha.getCode())));
    }
}

package application.api.response;

import application.persistence.model.CaptchaCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {
    private String secret;
    private String image;

    public CaptchaResponse(CaptchaCode captchaCode, String base64) {
        secret = captchaCode.getSecretCode();
        image = "data:image/png;base64, ".concat(base64);
    }
}

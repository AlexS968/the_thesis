package application.mapper;

import application.api.response.CaptchaResponse;
import application.model.CaptchaCode;
import com.github.cage.YCage;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class CaptchaMapper {

    public CaptchaResponse convertToDto(CaptchaCode captcha) throws Exception {
        BufferedImage img = resizeImage(new YCage().drawImage(captcha.getCode()),
                100, 35);
        byte[] resizedCaptcha;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "jpg", baos);
            resizedCaptcha = baos.toByteArray();
        }
        return new CaptchaResponse(captcha.getSecretCode(),
                "data:image/png;base64, " +
                        Base64.getEncoder().encodeToString(resizedCaptcha));
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }
}

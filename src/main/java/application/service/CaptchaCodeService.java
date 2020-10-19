package application.service;

import application.persistence.model.CaptchaCode;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface CaptchaCodeService {

    CaptchaCode captchaGenerator();

    String conversionToBase64(CaptchaCode captcha) throws IOException;

    boolean captchaIsValid(String code, String secretCode);

    static BufferedImage resizeImage(
            BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC,
                Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }
}

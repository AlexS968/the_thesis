package application.service;

import application.model.CaptchaCode;
import application.repository.CaptchaCodeRepository;
import application.service.interfaces.CaptchaService;
import com.github.cage.YCage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private final CaptchaCodeRepository captchaCodeRepository;

    @Value("${captchaExpirationTime}")
    public long capExpTime;

    public CaptchaServiceImpl(CaptchaCodeRepository captchaCodeRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
    }

    public CaptchaCode captchaGenerator() {
        //generate secret_code
        String passSymbols = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        final StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            randomString.append(passSymbols.charAt(rnd.nextInt(passSymbols.length())));
        }
        //create captcha
        CaptchaCode captcha = new CaptchaCode(
                LocalDateTime.now(),
                new YCage().getTokenGenerator().next().substring(0, 4), //generate code
                randomString.toString());
        //save captcha
        captchaCodeRepository.save(captcha);
        //delete old captcha
        Timestamp time = Timestamp.valueOf(LocalDateTime.now().minusHours(capExpTime));
        if (captchaCodeRepository.checkOldCaptcha(time)) {
            captchaCodeRepository.deleteAllOld(time);
        }
        return captcha;
    }

    public String conversionToBase64(CaptchaCode captcha) throws IOException {
        //resize image
        BufferedImage img = CaptchaService.resizeImage(new YCage().drawImage(captcha.getCode()),
                100, 35);
        //conversion image to byte[]
        byte[] resizedCaptcha;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "jpg", baos);
            resizedCaptcha = baos.toByteArray();
        }
        return Base64.getEncoder().encodeToString(resizedCaptcha);
    }
}

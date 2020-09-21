package application.service;

import application.model.CaptchaCode;
import application.repository.CaptchaCodeRepository;
import application.service.interfaces.CaptchaService;
import com.github.cage.YCage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final CaptchaCodeRepository captchaCodeRepository;
    @Value("${captchaExpirationTime}")
    public long capExpTime;

    @Override
    public CaptchaCode captchaGenerator() {
        //create captcha
        CaptchaCode captcha = new CaptchaCode(LocalDateTime.now(),
                new YCage().getTokenGenerator().next().substring(0, 4),
                RandomStringUtils.randomAlphanumeric(15));
        //save captcha
        captchaCodeRepository.save(captcha);
        //delete old captcha
        Timestamp time = Timestamp.valueOf(LocalDateTime.now().minusHours(capExpTime));
        if (captchaCodeRepository.checkOldCaptcha(time)) {
            captchaCodeRepository.deleteAllOld(time);
        }
        return captcha;
    }

    @Override
    public String conversionToBase64(CaptchaCode captcha) throws IOException {
        //resize image
        BufferedImage img = CaptchaService.resizeImage(new YCage()
                .drawImage(captcha.getCode()), 100, 35);
        //conversion image to byte[]
        byte[] resizedCaptcha;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "jpg", baos);
            resizedCaptcha = baos.toByteArray();
        }
        return Base64.getEncoder().encodeToString(resizedCaptcha);
    }
}

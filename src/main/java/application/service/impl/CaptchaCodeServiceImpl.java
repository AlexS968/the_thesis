package application.service.impl;

import application.persistence.model.CaptchaCode;
import application.persistence.repository.CaptchaCodeRepository;
import application.service.CaptchaCodeService;
import com.github.cage.YCage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CaptchaCodeServiceImpl implements CaptchaCodeService {

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
            captchaCodeRepository.deleteAllOldCaptcha(time);
        }
        return captcha;
    }

    @Override
    public String conversionToBase64(CaptchaCode captcha) throws IOException {
        //resize image
        BufferedImage img = CaptchaCodeService.resizeImage(new YCage()
                .drawImage(captcha.getCode()), 100, 35);
        //conversion image to byte[]
        byte[] resizedCaptcha;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "jpg", baos);
            resizedCaptcha = baos.toByteArray();
        }
        return Base64.getEncoder().encodeToString(resizedCaptcha);
    }

    @Override
    public boolean captchaIsValid(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code).orElseThrow(EntityNotFoundException::new)
                .getSecretCode().equals(secretCode);
    }
}

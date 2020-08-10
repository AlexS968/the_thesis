package application.service;

import application.model.CaptchaCode;
import application.repository.CaptchaCodeRepository;
import application.service.interfaces.CaptchaService;
import com.github.cage.YCage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
                new YCage().getTokenGenerator().next(), //generate code
                randomString.toString());

        captchaCodeRepository.save(captcha);
        //delete old captcha
        Timestamp time = Timestamp.valueOf(LocalDateTime.now().minusHours(capExpTime));
        if (captchaCodeRepository.checkOldCaptcha(time)) {
            captchaCodeRepository.deleteAllOld(time);
        }
        return captcha;
    }
}

package application.service;

import application.repository.CaptchaCodeRepository;
import application.repository.UserRepository;
import application.service.interfaces.RegisterService;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    public RegisterServiceImpl(UserRepository userRepository, CaptchaCodeRepository captchaCodeRepository) {
        this.userRepository = userRepository;
        this.captchaCodeRepository = captchaCodeRepository;
    }

    public boolean checkEmail(String email) {
        return userRepository.checkUserByEmail(email);
    }

    public boolean checkName(String name) {
        return userRepository.checkUserByName(name);
    }

    public boolean checkCaptcha(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code)
                .getSecretCode().equals(secretCode);
    }
}

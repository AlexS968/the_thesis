package application.service;

import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.User;
import application.repository.CaptchaCodeRepository;
import application.repository.UserRepository;
import application.service.interfaces.RegisterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    public RegisterServiceImpl(UserRepository userRepository, CaptchaCodeRepository captchaCodeRepository) {
        this.userRepository = userRepository;
        this.captchaCodeRepository = captchaCodeRepository;
    }

    public void checkAndCreateUser(String email, String name, String code,
                                   String secretCode, String password) {
        if (!userRepository.checkUserByEmail(email)) {
            throw new ApiValidationException(new ApiValidationError(null,
                    null, null, "Имя указано неверно"), "");
        }
        if (!userRepository.checkUserByName(name)) {
            throw new ApiValidationException(new ApiValidationError(null,
                    null, "Этот e-mail уже зарегистрирован", null), "");
        }
        if (!captchaCodeRepository.findByCode(code).getSecretCode().equals(secretCode)) {
            throw new ApiValidationException(new ApiValidationError(null,
                    "Код с картинки введён неверно", null, null), "");
        }
        if (password.length() < 6) {
            throw new ApiValidationException(new ApiValidationError("Пароль короче 6-ти символов",
                    null, null, null), "");
        }
        userRepository.save(new User(name, email, password,
                LocalDateTime.now(), false));
    }

    public boolean checkCaptcha(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code)
                .getSecretCode().equals(secretCode);
    }
}

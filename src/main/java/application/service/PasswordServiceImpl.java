package application.service;

import application.api.request.ChangePasswordRequest;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.User;
import application.repository.CaptchaCodeRepository;
import application.service.interfaces.PasswordService;
import application.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    public PasswordServiceImpl(UserRepository userRepository, CaptchaCodeRepository captchaCodeRepository) {
        this.userRepository = userRepository;
        this.captchaCodeRepository = captchaCodeRepository;
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ApiValidationException(new ApiValidationError("\"Ссылка для восстановления пароля устарела. " +
                        "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>\"", null, null), ""));

        if (!captchaCodeRepository.findByCode(request.getCaptcha()).getSecretCode()
                .equals(request.getCaptchaSecret())) {
            throw new ApiValidationException(new ApiValidationError(
                    null, null, "Код с картинки введён неверно"), "");
        }
        if (request.getPassword().length() < 6) {
            throw new ApiValidationException(new ApiValidationError(
                    null, "Пароль короче 6-ти символов", null), "");
        }

        user.setPassword(request.getPassword());
        userRepository.save(user);
    }
}

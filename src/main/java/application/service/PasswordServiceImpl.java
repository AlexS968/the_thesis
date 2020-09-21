package application.service;

import application.api.request.ChangePasswordRequest;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.User;
import application.repository.CaptchaCodeRepository;
import application.repository.UserRepository;
import application.service.interfaces.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Override
    public void changePassword(ChangePasswordRequest request) {
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //check password recovery link
        if (userRepository.findByCode(request.getCode()).isEmpty()) {
            apiValidationError.setCode("\"Password recovery link is out of date. " +
                    "<a href=\\\"/auth/restore\\\">Request link again</a>\"");
            throwException = true;
        }
        //check secret code
        if (captchaCodeRepository.findByCode(request.getCaptcha()).isEmpty() ||
                !captchaCodeRepository.findByCode(request.getCaptcha()).get().getSecretCode()
                        .equals(request.getCaptchaSecret())) {
            apiValidationError.setCaptcha("Code from the picture is entered incorrectly");
            throwException = true;
        }
        //check password
        if (request.getPassword().length() < 6) {
            apiValidationError.setPassword("Password is shorter than 6 characters");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError, "");
        } else {
            User user = userRepository.findByCode(request.getCode()).get();
            user.setPassword(request.getPassword());
            userRepository.save(user);
        }
    }
}

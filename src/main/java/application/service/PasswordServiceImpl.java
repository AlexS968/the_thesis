package application.service;

import application.api.request.ChangePasswordRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.response.ResultResponse;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.User;
import application.model.repository.CaptchaCodeRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.PasswordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final SendGridMailServiceImpl sendGridMailService;

    @Override
    public ResultResponse changePassword(ChangePasswordRequest request) {
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
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse restorePassword(PasswordRestoreRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiValidationException("There is no such email"));
        String restoreCode = RandomStringUtils.randomAlphanumeric(45);
        user.setCode(restoreCode);
        userRepository.save(user);
        // send link by email
        sendGridMailService.sendMail(request.getEmail(), "Password restore link to the DevPub blog",
                "https://sablin-java-skillbox.herokuapp.com/login/change-password/" + restoreCode);
        return new ResultResponse(true);
    }
}

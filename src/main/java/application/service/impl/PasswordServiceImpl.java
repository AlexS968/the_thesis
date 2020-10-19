package application.service.impl;

import application.api.request.ChangePasswordRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.response.ResultResponse;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.apierror.ApiValidationError;
import application.persistence.model.User;
import application.service.PasswordService;
import application.service.SendGridMailService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final SendGridMailService sendGridMailService;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final CaptchaCodeServiceImpl captchaCodeService;

    @Override
    public ResultResponse changePassword(ChangePasswordRequest request) {
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //check password recovery link
        if (userService.findByCode(request.getCode()).isEmpty()) {
            apiValidationError.setCode("\"Password recovery link is out of date. " +
                    "<a href=\\\"/auth/restore\\\">Request link again</a>\"");
            throwException = true;
        }
        //check secret code
        if (!captchaCodeService.captchaIsValid(request.getCaptcha(), request.getCaptchaSecret())) {
            apiValidationError.setCaptcha("Code from the picture is entered incorrectly");
            throwException = true;
        }
        //check password
        if (request.getPassword().length() < 6) {
            apiValidationError.setPassword("Password is shorter than 6 characters");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError);
        } else {
            User user = userService.findByCode(request.getCode()).get();
            user.setPassword(encoder.encode(request.getPassword()));
            userService.save(user);
        }
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse restorePassword(PasswordRestoreRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("There is no such email"));
        String restoreCode = RandomStringUtils.randomAlphanumeric(45);
        user.setCode(restoreCode);
        userService.save(user);
        // send link by email
        sendGridMailService.sendMail(request.getEmail(), "Password restore link to the DevPub blog",
                "https://sablin-java-skillbox.herokuapp.com/login/change-password/" + restoreCode);
        return new ResultResponse(true);
    }
}

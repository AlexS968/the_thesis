package application.mapper;

import application.api.request.RegisterRequest;
import application.api.response.RegisterResponse;
import application.model.User;
import application.service.UserServiceImpl;
import application.service.interfaces.RegisterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegisterMapper {

    private final RegisterService registerService;
    private final UserServiceImpl userService;

    public RegisterMapper(RegisterService registerService, UserServiceImpl userService) {
        this.registerService = registerService;
        this.userService = userService;
    }

    public RegisterResponse convertToDto(RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        boolean result = true;
        if (registerService.checkName(request.getName())) {
            response.setErrorsName("Имя указано неверно");
            result = false;
        }
        if (registerService.checkEmail(request.getEmail())) {
            response.setErrorsEmail("Этот e-mail уже зарегистрирован");
            result = false;
        }
        if (!registerService.checkCaptcha(request.getCaptcha(), request.getCaptchaSecret())) {
            response.setErrorsCaptcha("Код с картинки введён неверно");
            result = false;
        }
        if (request.getPassword().length() < 6) {
            response.setErrorsPassword("Пароль короче 6-ти символов");
            result = false;
        }
        //если все ок, создаем нового юзера
        if (result) {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRegTime(LocalDateTime.now());
            user.setModerator(false);
            userService.saveUser(user);
        }
        response.setResult(result);
        return response;
    }
}

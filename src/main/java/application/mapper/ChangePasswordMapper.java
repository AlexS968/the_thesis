package application.mapper;

import application.api.request.ChangePasswordRequest;
import application.api.response.ChangePasswordResponse;
import application.model.User;
import application.service.RegisterServiceImpl;
import application.service.UserServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordMapper {

    private final RegisterServiceImpl registerService;
    private final UserServiceImpl userService;

    public ChangePasswordMapper(RegisterServiceImpl registerService, UserServiceImpl userService) {
        this.registerService = registerService;
        this.userService = userService;
    }

    public ChangePasswordResponse convertToDto(ChangePasswordRequest request) {
        ChangePasswordResponse response = new ChangePasswordResponse();
        User user = userService.findUserByCode(request.getCode());
        boolean result = true;

        if (user == null) {
            response.setErrorsCode("\"Ссылка для восстановления пароля устарела. " +
                    "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>\"");
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
        //если все ок, меняем пароль
        if (result) {
            user.setPassword(request.getPassword());
            userService.saveUser(user);
        }

        response.setResult(result);
        return response;
    }
}

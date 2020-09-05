package application.service;

import application.api.request.RegisterRequest;
import application.exception.ApiValidationException;
import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.exception.apierror.ApiValidationError;
import application.mapper.CaptchaMapper;
import application.model.User;
import application.repository.CaptchaCodeRepository;
import application.repository.UserRepository;
import application.service.interfaces.CaptchaService;
import application.service.interfaces.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public RegisterServiceImpl(UserRepository userRepository, UserServiceImpl userService, CaptchaCodeRepository captchaCodeRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.captchaCodeRepository = captchaCodeRepository;
    }

    public void createUser(RegisterRequest request) {
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        if (userRepository.isUserEmailExist(request.getEmail())) {
            apiValidationError.setEmail("Этот e-mail уже зарегистрирован");
            throwException = true;
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            apiValidationError.setName("Имя указано неверно");
            throwException = true;
        }
        if (!captchaCodeRepository.findByCode(request.getCaptcha()).getSecretCode()
                .equals(request.getCaptchaSecret())) {
            apiValidationError.setCaptcha("Код с картинки введён неверно");
            throwException = true;
        }
        if (request.getPassword().length() < 6) {
            apiValidationError.setPassword("Пароль короче 6-ти символов");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError, "");
        }
        userRepository.save(new User(request.getName(), request.getEmail(), request.getPassword(),
                LocalDateTime.now(), false));
    }

    public void changeProfile(MultipartFile file, Integer removePhoto, String password,
                              String name, String email, HttpSession session) throws Exception {
        //check authentication
        if (LoginServiceImpl.getSessionsId().get(session.getId()) == null) {
            throw new UserUnauthenticatedException();
        }
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntNotFoundException::new);

        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //save new email
        if (email != null) {
            if (!email.equals(user.getEmail())) {
                if (userRepository.findByEmail(email).isEmpty()) {
                    user.setEmail(email);
                } else {
                    apiValidationError.setEmail("Этот e-mail уже зарегистрирован");
                    throwException = true;
                }
            }
        }
        //save new name
        if (name == null || name.isEmpty()) {
            apiValidationError.setName("Имя указано неверно");
            throwException = true;
        } else {
            user.setName(name);
        }
        //save new password
        if (password != null) {
            if (password.length() >= 6) {
                user.setPassword(password);
            } else {
                apiValidationError.setPassword("Пароль короче 6-ти символов");
                throwException = true;
            }
        }
        // remove avatar
        if (removePhoto != null) {
            if (removePhoto == 1) {
                user.setPhoto(null);
            }
        }
        //load new avatar
        if (file != null) {
            if (file.getSize() < 5242880L) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                String avtPath = uploadPath + "/avatar";
                File avtDir = new File(avtPath);
                if (!avtDir.exists()) {
                    avtDir.mkdir();
                }
                //upload photo
                String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                File avatar = new File(avtPath + "/" + fileName);
                file.transferTo(avatar);
                //resize photo
                BufferedImage originalImage = ImageIO.read(avatar);
                BufferedImage resizedImage = CaptchaService.resizeImage(originalImage, 36, 36);
                ImageIO.write(resizedImage, "jpg", avatar);
                //set avatar
                user.setPhoto("/upload/avatar/" + fileName);
            } else {
                apiValidationError.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                throwException = true;
            }
        }
        //if something is wrong, throw exception
        if (throwException) {
            throw new ApiValidationException(apiValidationError, "User ID: " + user.getId());
        }
        userRepository.save(user);
    }

    public boolean checkCaptcha(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code)
                .getSecretCode().equals(secretCode);
    }
}

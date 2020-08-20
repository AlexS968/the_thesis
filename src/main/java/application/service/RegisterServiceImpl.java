package application.service;

import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.mapper.CaptchaMapper;
import application.model.User;
import application.repository.CaptchaCodeRepository;
import application.repository.UserRepository;
import application.service.interfaces.RegisterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;

    @Value("${upload.path}")
    private String uploadPath;

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

    public void changeProfile(MultipartFile file, Integer removePhoto, String password,
                              String name, String email, User user) throws Exception {

        ApiValidationError error = new ApiValidationError();

        if (email != null) {
            if (!email.equals(user.getEmail())) {
                if (userRepository.findByEmail(email).isEmpty()) {
                    user.setEmail(email);
                } else {
                    error.setEmail("Этот e-mail уже зарегистрирован");
                    throw new ApiValidationException(error, "User ID: " + user.getId());
                }
            }
        }
        if (name == null || name.isEmpty()) {
            error.setName("Имя указано неверно");
            throw new ApiValidationException(error, "User ID: " + user.getId());
        } else {
            user.setName(name);
        }
        if (password != null) {
            if (password.length() >= 6) {
                user.setPassword(password);
            } else {
                error.setPassword("Пароль короче 6-ти символов");
                throw new ApiValidationException(error, "User ID: " + user.getId());
            }
        }
        if (removePhoto != null) {
            if (removePhoto == 1) {
                user.setPhoto(null);
            }
        }
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
                BufferedImage resizedImage = CaptchaMapper.resizeImage(originalImage, 36, 36);
                ImageIO.write(resizedImage, "jpg", avatar);
                //set avatar
                user.setPhoto("/upload/avatar/" + fileName);
            } else {
                error.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                throw new ApiValidationException(error, "User ID: " + user.getId());
            }
        }
        userRepository.save(user);
    }

    public boolean checkCaptcha(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code)
                .getSecretCode().equals(secretCode);
    }
}

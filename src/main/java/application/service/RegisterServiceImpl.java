package application.service;

import application.api.request.RegisterRequest;
import application.api.response.ResultResponse;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.model.User;
import application.model.repository.CaptchaCodeRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.RegisterService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final PasswordEncoder encoder;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public ResultResponse createUser(RegisterRequest request) {
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //check email
        if (userRepository.isUserEmailExist(request.getEmail())) {
            apiValidationError.setEmail("User with this email already exists");
            throwException = true;
        }
        //check name
        if (request.getName() == null || request.getName().isEmpty()) {
            apiValidationError.setName("Name is incorrect");
            throwException = true;
        }
        //check captcha
        if (captchaCodeRepository.findByCode(request.getCaptcha()).isEmpty() ||
                !captchaCodeRepository.findByCode(request.getCaptcha()).get()
                        .getSecretCode().equals(request.getCaptchaSecret())) {
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
        }
        userRepository.save(new User(request.getName(), request.getEmail(), encoder
                .encode(request.getPassword()), LocalDateTime.now(), false));
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse changeProfile(MultipartFile file, Integer removePhoto, String password,
                                        String name, String email, Principal principal) throws Exception {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //save new email
        if (email != null) {
            if (!email.equals(user.getEmail())) {
                if (userRepository.findByEmail(email).isEmpty()) {
                    user.setEmail(email);
                } else {
                    apiValidationError.setEmail("User with this email already exists");
                    throwException = true;
                }
            }
        }
        //save new name
        if (name == null || name.isEmpty()) {
            apiValidationError.setName("Name is incorrect");
            throwException = true;
        } else {
            user.setName(name);
        }
        //save new password
        if (password != null) {
            if (password.length() >= 6) {
                user.setPassword(encoder.encode(password));
            } else {
                apiValidationError.setPassword("Password is shorter than 6 characters");
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
                //create instance of Cloudinary to save avatar
                Map params = ObjectUtils.asMap(
                        "cloud_name", "dxywt3ld7",
                        "api_key", "372888264999633",
                        "api_secret", "ZJfDjL0K6wxdvtZr-cz0ua0-VSY",
                        "folder", "skillbox/avatar"
                );
                Cloudinary cloudinary = new Cloudinary(params);
                String base64DataURI = "data:image/png;base64,"
                        .concat(Base64.getEncoder().encodeToString(file.getBytes()));
                Map result = cloudinary.uploader().upload(base64DataURI, params);
                String imageTag = result.get("url").toString().split("/")[9];
                user.setPhoto("/upload/c_fill,g_faces,h_36,w_36/skillbox/avatar/" + imageTag);
            } else {
                apiValidationError.setPhoto("Photo is too large, need no more than 5 Mb");
                throwException = true;
            }
        }
        //if something is wrong, throw exception
        if (throwException) {
            throw new ApiValidationException(apiValidationError, "User ID: " + user.getId());
        }
        userRepository.save(user);
        return new ResultResponse(true);
    }

    @Override
    public boolean checkCaptcha(String code, String secretCode) {
        return captchaCodeRepository.findByCode(code).isPresent() &
                captchaCodeRepository.findByCode(code).get().getSecretCode()
                        .equals(secretCode);
    }
}

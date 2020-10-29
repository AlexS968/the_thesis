package application.service.impl;

import application.api.request.RegisterRequest;
import application.api.response.ResultResponse;
import application.exception.ApiValidationException;
import application.exception.apierror.ApiValidationError;
import application.persistence.model.User;
import application.service.CaptchaCodeService;
import application.service.GlobalSettingService;
import application.service.RegisterService;
import application.service.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;
    private final CaptchaCodeService captchaCodeService;
    private final GlobalSettingService globalSettingService;
    private final PasswordEncoder encoder;

    @Value("${upload.cloudinary.path}")
    private String uploadPath;
    @Value("${upload.cloudinary.cloudName}")
    private String cloudName;
    @Value("${upload.cloudinary.apiKey}")
    private String apiKey;
    @Value("${upload.cloudinary.apiSecret}")
    private String apiSecret;

    @Override
    public ResultResponse createUser(RegisterRequest request) {
        if (!globalSettingService.multiUserModeEnabled()) {
            throw new EntityNotFoundException();
        }
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //check email
        if (userService.isUserEmailExist(request.getEmail())) {
            apiValidationError.setEmail("User with this email already exists");
            throwException = true;
        }
        //check name
        if (request.getName() == null || request.getName().isEmpty()) {
            apiValidationError.setName("Name is incorrect");
            throwException = true;
        }
        //check captcha
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
        }
        userService.save(new User(request.getName(), request.getEmail(), encoder
                .encode(request.getPassword()), LocalDateTime.now(), false));
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse changeProfile(MultipartFile file, Integer removePhoto, String password,
                                        String name, String email, Principal principal) throws Exception {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        //save new email
        if (email != null) {
            if (!email.equals(user.getEmail())) {
                if (userService.findByEmail(email).isEmpty()) {
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
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret,
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
            throw new ApiValidationException(apiValidationError);
        }
        userService.save(user);
        return new ResultResponse(true);
    }
}

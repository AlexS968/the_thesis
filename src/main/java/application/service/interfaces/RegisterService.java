package application.service.interfaces;

import application.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RegisterService {

    void checkAndCreateUser(String email, String name, String code,
                            String secretCode, String password);

    void changeProfile(MultipartFile file, Integer removePhoto, String password,
                       String name, String email, User user) throws Exception;

    boolean checkCaptcha(String code, String secretCode);
}

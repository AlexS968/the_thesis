package application.service.interfaces;

import application.api.request.RegisterRequest;
import application.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

public interface RegisterService {

    void createUser(RegisterRequest request);

    void changeProfile(MultipartFile file, Integer removePhoto, String password,
                       String name, String email, HttpSession session) throws Exception;

    boolean checkCaptcha(String code, String secretCode);
}

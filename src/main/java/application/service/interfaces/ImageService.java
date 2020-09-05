package application.service.interfaces;

import application.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile file, HttpSession session) throws IOException;
}

package application.service.interfaces;

import application.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile file, User user) throws IOException;
}

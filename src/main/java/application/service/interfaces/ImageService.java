package application.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

public interface ImageService {

    String uploadImage(MultipartFile file, Principal principal) throws IOException;
}

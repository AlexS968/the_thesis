package application.service;

import application.model.User;
import application.service.interfaces.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${upload.path}")
    private String uploadPath;

    public String uploadImage(MultipartFile file, User user) throws IOException {
        String pathname = null;
        if (file != null) {
            String path = UUID.randomUUID().toString();
            String firstDir = path.substring(0, 7);
            String secondDir = path.substring(9, 12);
            String thirdDir = path.substring(14, 17);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            uploadDir = new File(uploadPath + "/" + firstDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            uploadDir = new File(uploadPath + "/" + firstDir + "/" + secondDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            uploadDir = new File(uploadPath + "/" + firstDir + "/" + secondDir + "/" + thirdDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String resultFilename = path.substring(19) + file.getOriginalFilename();
            pathname = "/upload/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + resultFilename;
            file.transferTo(new File(uploadPath + "/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + resultFilename));
        }
        return pathname;
    }
}

package application.service;

import application.exception.BadRequestException;
import application.exception.UserUnauthenticatedException;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.service.interfaces.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${upload.path}")
    private String uploadPath;

    public String uploadImage(MultipartFile file, HttpSession session) throws IOException {
        String pathname = null;
        //check authentication
        if (LoginServiceImpl.getSessionsId().get(session.getId()) == null) {
            throw new UserUnauthenticatedException();
        }
        //save picture and return link
        if (file != null) {
            ApiValidationError validationError = new ApiValidationError();
            //check file size
            if (file.getSize() > 5242880L) {
                validationError.setImage("Размер файла превышает допустимый размер");
                throw new BadRequestException(new ApiError(false, validationError), "");
            }
            //check file type
            String fileType = Objects.requireNonNull(file.getContentType()).substring(6).trim();
            if (!fileType.equals("jpeg") & !fileType.equals("png")) {
                validationError.setImage("Недопустимый тип файла");
                throw new BadRequestException(new ApiError(false, validationError), "");
            }
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

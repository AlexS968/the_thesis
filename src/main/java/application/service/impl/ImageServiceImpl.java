package application.service.impl;

import application.exception.BadRequestException;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.service.ImageService;
import application.service.UserService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final UserService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file, Principal principal) throws IOException {
        if (file == null) {
            throw new BadRequestException("File missing!");
        }
        //check authentication
        if (userService.findByEmail(principal.getName()).isPresent()) {
            ApiValidationError validationError = new ApiValidationError();
            //check file size
            if (file.getSize() > 5242880L) {
                validationError.setImage("File is too large, need no more than 5 Mb");
                throw new BadRequestException(validationError);
            }
            //check file type
            String fileType = Objects.requireNonNull(file.getContentType()).substring(6).trim();
            if (!fileType.equals("jpeg") & !fileType.equals("png")) {
                validationError.setImage("Invalid file type");
                throw new BadRequestException(validationError);
            }
            //create random path and instance of Cloudinary with map of parameters to upload an image to Cloudinary
            String path = "skillbox".concat(File.separator).concat(RandomStringUtils.randomAlphanumeric(2))
                    .concat(File.separator).concat(RandomStringUtils.randomAlphanumeric(2))
                    .concat(File.separator).concat(RandomStringUtils.randomAlphanumeric(2));
            Map params = ObjectUtils.asMap(
                    "cloud_name", "dxywt3ld7",
                    "api_key", "372888264999633",
                    "api_secret", "ZJfDjL0K6wxdvtZr-cz0ua0-VSY",
                    "folder", path
            );
            Cloudinary cloudinary = new Cloudinary(params);
            String base64DataURI = "data:image/png;base64,"
                    .concat(Base64.getEncoder().encodeToString(file.getBytes()));
            Map result = cloudinary.uploader().upload(base64DataURI, params);
            return "/upload".concat(result.get("url").toString().split("upload")[1]);
        } else {
            throw new UsernameNotFoundException(principal.getName());
        }
    }
}

package application.service;

import application.api.request.RegisterRequest;
import application.api.response.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface RegisterService {

    ResultResponse createUser(RegisterRequest request);

    ResultResponse changeProfile(MultipartFile file, Integer removePhoto, String password,
                                 String name, String email, Principal principal) throws Exception;
}

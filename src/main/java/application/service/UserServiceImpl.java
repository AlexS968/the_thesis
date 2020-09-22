package application.service;

import application.api.request.PasswordRestoreRequest;
import application.exception.ApiValidationException;
import application.exception.UserUnauthenticatedException;
import application.model.User;
import application.repository.UserRepository;
import application.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SendGridMailServiceImpl sendGridMailService;

    @Override
    public User findUserByCode(String code) {
        return userRepository.findByCode(code).orElseThrow(() -> new ApiValidationException(""));
    }

    @Override
    public Optional<User> findUserById(Long id) {
        if (id == null) {
            throw new UserUnauthenticatedException();
        }
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void restorePassword(PasswordRestoreRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiValidationException("There is no such email"));
        String restoreCode = RandomStringUtils.randomAlphanumeric(45);
        user.setCode(restoreCode);
        userRepository.save(user);
        // send link by email
        sendGridMailService.sendMail(request.getEmail(), "Password restore link to the DevPub blog",
                "https://sablin-java-skillbox.herokuapp.com/login/change-password/" + restoreCode);
    }
}

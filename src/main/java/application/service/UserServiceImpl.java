package application.service;

import application.api.request.PasswordRestoreRequest;
import application.exception.ApiValidationException;
import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.model.User;
import application.repository.UserRepository;
import application.service.interfaces.UserService;
import com.sun.istack.NotNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JavaMailSender emailSender;

    public UserServiceImpl(UserRepository userRepository, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

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

    public void restorePassword(PasswordRestoreRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(EntNotFoundException::new);

        String charsCaps = "abcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String passSymbols = charsCaps + nums;
        Random rnd = new Random();
        final StringBuilder hash = new StringBuilder();
        for (int i = 0; i < 45; i++) {
            hash.append(passSymbols.charAt(rnd.nextInt(passSymbols.length())));
        }
        user.setCode(hash.toString());
        userRepository.save(user);
        // send link by email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Ссылка на восстановление пароля MyBlog");
        message.setText("http://localhost:8080/login/change-password/" + hash.toString());
        // send Message!
        this.emailSender.send(message);
    }
}

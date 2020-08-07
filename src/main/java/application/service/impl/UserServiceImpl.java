package application.service.impl;

import application.model.User;
import application.repository.UserRepository;
import application.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean checkUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
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
            return true;
        } else {
            return false;
        }
    }
}

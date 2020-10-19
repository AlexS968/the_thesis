package application.service.impl;

import application.persistence.model.User;
import application.persistence.repository.UserRepository;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByCode(String code) {
        return userRepository.findByCode(code);
    }

    @Override
    public boolean isUserEmailExist(String email) {
        return userRepository.isUserEmailExist(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }


}

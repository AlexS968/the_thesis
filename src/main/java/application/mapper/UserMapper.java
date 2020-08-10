package application.mapper;

import application.api.response.LoginResponse;
import application.api.response.UserByLoginResponse;
import application.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public LoginResponse convertToDto(User user) {
        LoginResponse response = new LoginResponse();
        UserByLoginResponse userResponse = new UserByLoginResponse();
        if (user != null) {
            modelMapper().map(user, userResponse);
            response.setResult(true);
            response.setUser(userResponse);
        } else {
            response.setResult(false);
        }
        return response;
    }
}

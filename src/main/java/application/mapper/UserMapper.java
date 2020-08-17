package application.mapper;

import application.api.response.AuthenticationResponse;
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

    public AuthenticationResponse convertToDto(User user, int moderationCount) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setResult(true);
        response.setUser();
        modelMapper().map(user, response);
        response.setUserModeration(user.isModerator());
        //****************** не понятно, что такое settings здесь???? ************
        response.setUserSettings(false);
        response.setUserModerationCount(moderationCount);
        return response;
    }
}

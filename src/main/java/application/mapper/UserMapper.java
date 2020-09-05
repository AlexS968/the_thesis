package application.mapper;

import application.api.response.AuthenticationResponse;
import application.exception.EntNotFoundException;
import application.model.User;
import application.service.LoginServiceImpl;
import application.service.PostServiceImpl;
import application.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserMapper {

    private final UserServiceImpl userService;
    private final PostServiceImpl postService;

    public UserMapper(UserServiceImpl userService, PostServiceImpl postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @Bean
    public ModelMapper userModelMapper() {
        return new ModelMapper();
    }

    public AuthenticationResponse convertToDto(HttpSession session) {
        AuthenticationResponse response = new AuthenticationResponse();
        Long userId = LoginServiceImpl.getSessionsId().get(session.getId());
        if (userId == null) {
            response.setResult(false);
        } else {
            response.setResult(true);
            User user = userService.findUserById(userId)
                    .orElseThrow(EntNotFoundException::new);
            response.setUser();
            userModelMapper().map(user, response);
            response.setUserModeration(user.isModerator());
            response.setUserSettings(false);
            response.setUserModerationCount(!user.isModerator() ?
                    0 : postService.getPostsForModeration(session, "new").size());
        }
        return response;
    }
}

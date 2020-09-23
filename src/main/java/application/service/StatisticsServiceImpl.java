package application.service;

import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.model.Post;
import application.model.User;
import application.repository.PostRepository;
import application.service.interfaces.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PostRepository postRepository;
    private final UserServiceImpl userService;
    private final GlobalSettingServiceImpl globalSettingService;

    public List<Post> getAllPostsOrderByTimeAsc(HttpSession session)
            throws UserUnauthenticatedException {
        long userId = LoginServiceImpl.getSessionsId().getOrDefault(session.getId(), -1L);
        boolean userAuthenticatedAndModerator = userService.findUserById(userId).isPresent()
                && userService.findUserById(userId).get().isModerator();
        //if statistics is not public and user is not moderator throw exception
        if (!globalSettingService.statisticsIsPublic() & !userAuthenticatedAndModerator) {
            throw new UserUnauthenticatedException();
        }
        return new ArrayList<>(postRepository.findAllOrderByTimeAsc());
    }

    public List<Post> getAllPostsByUserIdOrderByTimeAsc(HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntNotFoundException::new);
        return new ArrayList<>(postRepository
                .findAllByUserIdOrderByTimeAsc(user.getId()));
    }
}

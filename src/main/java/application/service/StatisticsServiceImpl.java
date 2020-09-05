package application.service;

import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.model.Post;
import application.repository.PostRepository;
import application.service.interfaces.StatisticsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PostRepository postRepository;
    private final UserServiceImpl userService;
    private final GlobalSettingServiceImpl globalSettingService;

    public StatisticsServiceImpl(PostRepository postRepository, UserServiceImpl userService, GlobalSettingServiceImpl globalSettingService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.globalSettingService = globalSettingService;
    }

    public List<Post> getAllPostsOrderByTimeAsc(HttpSession session) {
        boolean userIsModerator;
        long userId = LoginServiceImpl.getSessionsId().getOrDefault(session.getId(), -1L);
        if (userId == -1L) {
            userIsModerator=false;
        } else {
            userIsModerator=userService.findUserById(LoginServiceImpl.getSessionsId()
                    .get(session.getId())).orElseThrow(EntNotFoundException::new).isModerator();
        }
       //if statistics is not public and user is not moderator throw exception
        if (!globalSettingService.statisticsIsPublic() & !userIsModerator) {
            throw new UserUnauthenticatedException();
        }
        return new ArrayList<>(postRepository.findAllOrderByTimeAsc());
    }

    public List<Post> getAllPostsByUserIdOrderByTimeAsc(HttpSession session) {
        long userId = LoginServiceImpl.getSessionsId().getOrDefault(session.getId(), -1L);
        if (userId == -1L) {
            throw new UserUnauthenticatedException();
        }
        return new ArrayList<>(postRepository.findAllByUserIdOrderByTimeAsc(userId));
    }
}

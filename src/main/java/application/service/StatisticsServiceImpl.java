package application.service;

import application.exception.UserUnauthenticatedException;
import application.model.Post;
import application.model.User;
import application.model.repository.PostRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final GlobalSettingServiceImpl globalSettingService;

    public List<Post> getAllPostsOrderByTimeAsc(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        boolean userAuthenticatedAndModerator = (user != null) && user.isModerator();
        //if statistics is not public and user is not moderator throw exception
        if (!globalSettingService.statisticsIsPublic() & !userAuthenticatedAndModerator) {
            throw new UserUnauthenticatedException();
        }
        return new ArrayList<>(postRepository.findAllOrderByTimeAsc());
    }

    public List<Post> getAllPostsByUserIdOrderByTimeAsc(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("principal.getName()"));
        return new ArrayList<>(postRepository.findAllByUserIdOrderByTimeAsc(user.getId()));
    }
}

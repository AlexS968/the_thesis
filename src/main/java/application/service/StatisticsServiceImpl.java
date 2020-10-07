package application.service;

import application.model.Post;
import application.model.User;
import application.model.repository.PostRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.GlobalSettingService;
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
    private final GlobalSettingService globalSettingService;

    public List<Post> getAllPostsOrderByTimeAsc(Principal principal) {
        boolean userAuthenticatedAndModerator = principal != null && userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName())).isModerator();
        //if statistics is not public and user is not moderator throw exception
        if (!globalSettingService.statisticsIsPublic() & !userAuthenticatedAndModerator) {
            throw new UsernameNotFoundException(null);
        }
        return new ArrayList<>(postRepository.findAllOrderByTimeAsc());
    }

    public List<Post> getAllPostsByUserIdOrderByTimeAsc(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        return new ArrayList<>(postRepository.findAllByUserIdOrderByTimeAsc(user.getId()));
    }
}

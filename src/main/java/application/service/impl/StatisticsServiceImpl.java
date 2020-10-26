package application.service.impl;

import application.persistence.model.Post;
import application.persistence.model.User;
import application.service.GlobalSettingService;
import application.service.PostService;
import application.service.StatisticsService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final GlobalSettingService globalSettingService;
    private final PostService postService;
    private final UserService userService;

    @Override
    public List<Post> getAllPostsOrderByTimeAsc(Principal principal) {
        boolean userAuthenticatedAndModerator = principal != null && userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName())).isModerator();
        //if statistics is not public and user is not moderator throw exception
        if (!globalSettingService.statisticsIsPublic() & !userAuthenticatedAndModerator) {
            throw new UsernameNotFoundException(null);
        }
        return new ArrayList<>(postService.findAllOrderByTimeAsc());
    }

    @Override
    public List<Post> getAllPostsByUserIdOrderByTimeAsc(Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        return new ArrayList<>(postService.findAllByUserIdOrderByTimeAsc(user.getId()));
    }
}

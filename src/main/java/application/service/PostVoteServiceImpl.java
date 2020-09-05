package application.service;

import application.exception.UserUnauthenticatedException;
import application.model.Post;
import application.model.PostVote;
import application.model.User;
import application.repository.PostVoteRepository;
import application.service.interfaces.PostVoteService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class PostVoteServiceImpl implements PostVoteService {

    private final PostVoteRepository postVoteRepository;
    private final UserServiceImpl userService;

    public PostVoteServiceImpl(PostVoteRepository postVoteRepository, UserServiceImpl userService) {
        this.postVoteRepository = postVoteRepository;
        this.userService = userService;
    }

    public boolean like(Post post, HttpSession session, boolean like) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        PostVote postVote = postVoteRepository.findByPostIdAndByUserId(post.getId(), user.getId());
        boolean result;
        if (postVote == null) {
            postVoteRepository.save(new PostVote(user, post, LocalDateTime.now(), like));
            result = true;
        } else if (postVote.isValue() != like) {
            postVote.setValue(like);
            postVote.setTime(LocalDateTime.now());
            postVoteRepository.save(postVote);
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}

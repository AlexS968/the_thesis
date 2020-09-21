package application.service;

import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.model.Post;
import application.model.PostVote;
import application.model.User;
import application.repository.PostVoteRepository;
import application.service.interfaces.PostVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {
    private final PostVoteRepository postVoteRepository;
    private final UserServiceImpl userService;

    @Override
    public boolean like(Post post, HttpSession session, boolean like) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        PostVote postVote = postVoteRepository.findByPostIdAndByUserId(post.getId(), user.getId())
                .orElse(null);
        if (postVote == null) {
            postVoteRepository.save(new PostVote(user, post, LocalDateTime.now(), like));
            return true;
        } else if (postVote.isValue() != like) {
            postVote.setValue(like);
            postVote.setTime(LocalDateTime.now());
            postVoteRepository.save(postVote);
            return true;
        } else return false;
    }
}

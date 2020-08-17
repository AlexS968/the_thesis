package application.service;

import application.model.Post;
import application.model.PostVote;
import application.model.User;
import application.repository.PostVoteRepository;
import application.service.interfaces.PostVoteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostVoteServiceImpl implements PostVoteService {

    private final PostVoteRepository postVoteRepository;

    public PostVoteServiceImpl(PostVoteRepository postVoteRepository) {
        this.postVoteRepository = postVoteRepository;
    }

    public boolean like(Post post, User user, boolean like) {
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

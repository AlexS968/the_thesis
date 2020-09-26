package application.service;

import application.model.Post;
import application.model.PostVote;
import application.model.User;
import application.model.repository.PostVoteRepository;
import application.model.repository.UserRepository;
import application.service.interfaces.PostVoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {
    private final PostVoteRepository postVoteRepository;
    private final UserRepository userRepository;

    @Override
    public boolean like(Post post, Principal principal, boolean like) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
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

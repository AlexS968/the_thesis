package application.service.impl;

import application.persistence.model.Post;
import application.persistence.model.PostVote;
import application.persistence.model.User;
import application.persistence.repository.PostVoteRepository;
import application.service.PostVoteService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostVoteServiceImpl implements PostVoteService {

    private final PostVoteRepository postVoteRepository;
    private final UserService userService;

    @Override
    public boolean like(Post post, Principal principal, boolean like) {
        User user = userService.findByEmail(principal.getName())
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

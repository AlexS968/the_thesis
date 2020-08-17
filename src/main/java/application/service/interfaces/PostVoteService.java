package application.service.interfaces;

import application.model.Post;
import application.model.User;

public interface PostVoteService {

    boolean like(Post post, User user, boolean like);
}

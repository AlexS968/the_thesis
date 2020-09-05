package application.service.interfaces;

import application.model.Post;
import application.model.User;

import javax.servlet.http.HttpSession;

public interface PostVoteService {

    boolean like(Post post, HttpSession session, boolean like);
}

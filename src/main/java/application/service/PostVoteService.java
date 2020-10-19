package application.service;

import application.persistence.model.Post;

import java.security.Principal;

public interface PostVoteService {

    boolean like(Post post, Principal principal, boolean like);
}

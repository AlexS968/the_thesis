package application.service;

import application.persistence.model.Post;

import java.security.Principal;
import java.util.List;

public interface StatisticsService {

    List<Post> getAllPostsOrderByTimeAsc(Principal principal);

    List<Post> getAllPostsByUserIdOrderByTimeAsc(Principal principal);
}

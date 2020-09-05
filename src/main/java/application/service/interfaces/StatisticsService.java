package application.service.interfaces;

import application.model.Post;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface StatisticsService {

    List<Post> getAllPostsOrderByTimeAsc(HttpSession session);

    List<Post> getAllPostsByUserIdOrderByTimeAsc(HttpSession session);
}

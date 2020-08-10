package application.service.interfaces;

import application.model.Post;

import java.util.List;

public interface StatisticsService {

    List<Post> getAllPostsOrderByTimeAsc();
}

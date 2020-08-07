package application.service;

import application.model.Post;

import java.util.List;

public interface StatisticsService {

    List<Post> getAllPostsOrderByTimeAsc();
}

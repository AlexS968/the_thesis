package application.service.impl;

import application.model.Post;
import application.repository.PostRepository;
import application.service.StatisticsService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final PostRepository postRepository;

    public StatisticsServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPostsOrderByTimeAsc() {
        return new ArrayList<>(postRepository.findAllOrderByTimeAsc());
    }
}

package application.service;

import application.model.Post;
import application.repository.PostRepository;
import application.service.interfaces.StatisticsService;
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

    public List<Post> getAllPostsByUserIdOrderByTimeAsc(long userId) {
        return new ArrayList<>(postRepository.findAllByUserIdOrderByTimeAsc(userId));
    }
}

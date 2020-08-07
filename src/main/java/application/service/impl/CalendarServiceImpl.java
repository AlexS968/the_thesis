package application.service.impl;

import application.repository.PostRepository;
import application.service.CalendarService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final PostRepository postRepository;

    public CalendarServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Map<String, Integer> postsByDay() {
        Map<String, Integer> posts = new HashMap<>();
        postRepository.countPostsByDay().forEach(element -> {
            posts.put(element.getName(), element.getTotal());
        });
        return posts;
    }
}

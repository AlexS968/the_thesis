package application.service;

import application.repository.PostRepository;
import application.service.interfaces.CalendarService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final PostRepository postRepository;

    public CalendarServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Map<String, Integer> postsByDayPerYear(Integer givenYear) {
        Map<String, Integer> posts = new HashMap<>();
        LocalDateTime from;
        LocalDateTime to;
        if (givenYear == null || givenYear == LocalDateTime.now().getYear()) {
            from = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
            to = LocalDateTime.now();
        } else {
            from = LocalDateTime.of(givenYear, 1, 1, 0, 0);
            to = LocalDateTime.of(givenYear + 1, 1, 1, 0, 0);
        }
        postRepository.countPostsByDay(Timestamp.valueOf(from), Timestamp.valueOf(to)).forEach(element -> {
            posts.put(element.getName(), element.getTotal());
        });
        return posts;
    }

    public LocalDateTime timeOfEarliestPost() {
        return postRepository.findEarliestPost().getTime();
    }
}

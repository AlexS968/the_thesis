package application.service;

import application.exception.EntNotFoundException;
import application.model.repository.PostRepository;
import application.service.interfaces.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
    private final PostRepository postRepository;

    @Override
    public Map<String, Integer> postsByDayPerYear(Integer givenYear) {
        Map<String, Integer> posts = new HashMap<>();
        LocalDateTime from, to;
        if (givenYear == null || givenYear == LocalDateTime.now().getYear()) {
            from = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
            to = LocalDateTime.now();
            System.out.println("TimeZone.getDefault в CalenderService "+TimeZone.getDefault());
        } else {
            from = LocalDateTime.of(givenYear, 1, 1, 0, 0);
            to = LocalDateTime.of(givenYear + 1, 1, 1, 0, 0);
        }
        postRepository.countPostsByDay(Timestamp.valueOf(from), Timestamp.valueOf(to)).forEach(element -> {
            posts.put(element.getName(), element.getTotal());
        });
        return posts;
    }

    @Override
    public LocalDateTime timeOfEarliestPost() {
        return postRepository.findEarliestPost()
                .orElseThrow(EntNotFoundException::new).getTime();
    }
}

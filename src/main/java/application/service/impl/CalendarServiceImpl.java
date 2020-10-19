package application.service.impl;

import application.service.CalendarService;
import application.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final PostService postService;

    @Override
    public Map<String, Integer> postsByDayPerYear(Integer givenYear) {
        Map<String, Integer> posts = new HashMap<>();
        LocalDateTime from, to;
        if (givenYear == null || givenYear == LocalDateTime.now().getYear()) {
            from = LocalDateTime.of(LocalDateTime.now().getYear(), 1, 1, 0, 0);
            to = LocalDateTime.now();
        } else {
            from = LocalDateTime.of(givenYear, 1, 1, 0, 0);
            to = LocalDateTime.of(givenYear + 1, 1, 1, 0, 0);
        }
        postService.countPostsByDay(Timestamp.valueOf(from), Timestamp.valueOf(to)).forEach(element -> {
            posts.put(element.getName(), element.getTotal());
        });
        return posts;
    }

    @Override
    public LocalDateTime timeOfEarliestPost() {
        return postService.findEarliestPost()
                .orElseThrow(EntityNotFoundException::new).getTime();
    }
}

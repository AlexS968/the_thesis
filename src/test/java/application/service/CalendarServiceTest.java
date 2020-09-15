package application.service;

import application.model.Post;
import application.repository.IPostCount;
import application.repository.PostRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CalendarServiceTest {

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CalendarServiceImpl calendarService;

    @Test
    public void shouldGetTimeOfEarliestPost() {
        LocalDateTime postTime = LocalDateTime.now().minusYears(100);
        Post post = new Post();
        post.setTime(postTime);
        Mockito.when(postRepository.findEarliestPost()).thenReturn(post);
        Assert.assertEquals(postTime, calendarService.timeOfEarliestPost());
    }

    @Test
    public void shouldGetPostsByDayPerYear() {
        List<IPostCount> iPostCountList = new ArrayList<>();
        iPostCountList.add(new IPostCount() {
            @Override
            public String getName() {
                return "2019-02-14";
            }
            @Override
            public Integer getTotal() {
                return 2;
            }
        });

        LocalDateTime from = LocalDateTime.of(2019, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2020, 1, 1, 0, 0);
        Mockito.when(postRepository.countPostsByDay(Timestamp.valueOf(from), Timestamp.valueOf(to)))
                .thenReturn(iPostCountList);

        Map<String, Integer> postsByDayPerYear = new HashMap<>();
        postsByDayPerYear.put("2019-02-14", 2);

        Assert.assertEquals(postsByDayPerYear,
                calendarService.postsByDayPerYear(2019));
    }
}

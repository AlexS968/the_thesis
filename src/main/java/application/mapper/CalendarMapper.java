package application.mapper;

import application.api.response.CalendarResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class CalendarMapper {

    public CalendarResponse convertToDto(Map<String, Integer> posts, LocalDateTime timeOfEarliestPost) {
        int firstYear = timeOfEarliestPost.getYear();
        int arraySize = LocalDateTime.now().getYear() - timeOfEarliestPost.getYear() + 1;
        Integer[] years = new Integer[arraySize];
        for (int i = 0; i < arraySize; i++) {
            years[i] = firstYear + i;
        }
        return new CalendarResponse(years, posts);
    }
}

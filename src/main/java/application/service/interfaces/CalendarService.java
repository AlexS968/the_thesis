package application.service.interfaces;

import java.time.LocalDateTime;
import java.util.Map;

public interface CalendarService {

    Map<String, Integer> postsByDayPerYear(Integer givenYear);

    LocalDateTime timeOfEarliestPost();
}

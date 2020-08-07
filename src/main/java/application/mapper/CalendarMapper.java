package application.mapper;

import application.dto.response.CalendarResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CalendarMapper {

    //дописать получение годов
    public CalendarResponse convertToDto(Map<String, Integer> posts) {
        Integer[] years = {2020};
        return new CalendarResponse(years,posts);
    }
}

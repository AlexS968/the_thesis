package application.controller;

import application.dto.response.*;
import application.mapper.CalendarMapper;
import application.mapper.GlobalSettingMapper;
import application.mapper.StatisticsMapper;
import application.mapper.TagMapper;
import application.service.impl.CalendarServiceImpl;
import application.service.impl.InitServiceImpl;
import application.service.impl.StatisticsServiceImpl;
import application.service.TagService;
import application.service.impl.GlobalSettingServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class ApiGeneralController {

    private final InitServiceImpl initService;
    private final GlobalSettingServiceImpl globalSettingService;
    private final GlobalSettingMapper settingMapper;
    private final TagService tagService;
    private final TagMapper tagMapper;
    private final CalendarServiceImpl calendarService;
    private final CalendarMapper calendarMapper;
    private final StatisticsServiceImpl statisticsService;
    private final StatisticsMapper statisticsMapper;

    public ApiGeneralController(InitServiceImpl initService, GlobalSettingServiceImpl globalSettingService, GlobalSettingMapper settingMapper, TagService tagService, TagMapper tagMapper, CalendarServiceImpl calendarService, CalendarMapper calendarMapper, StatisticsServiceImpl statisticsService, StatisticsMapper statisticsMapper) {
        this.initService = initService;
        this.globalSettingService = globalSettingService;
        this.settingMapper = settingMapper;
        this.tagService = tagService;
        this.tagMapper = tagMapper;
        this.calendarService = calendarService;
        this.calendarMapper = calendarMapper;
        this.statisticsService = statisticsService;
        this.statisticsMapper = statisticsMapper;
    }

    @GetMapping(value = "api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    @GetMapping(value = "api/settings")
    public ResponseEntity<GlobalSettingResponse> settings() {
        return new ResponseEntity<>(settingMapper.convertToDto(
                globalSettingService.getGlobalSettings()),
                HttpStatus.OK);
    }

    @GetMapping(value = "api/tag")
    public ResponseEntity<TagsResponse> tags(@RequestParam(required = false) String query) {
        return new ResponseEntity<>(
                new TagsResponse(tagMapper.convertToDto(tagService.getTags(query))),
                HttpStatus.OK);
    }

    @GetMapping(value = "api/calendar")
    public ResponseEntity<CalendarResponse> calendar(
            @RequestParam(required = false) int year) {
        return new ResponseEntity<>
                (calendarMapper.convertToDto(calendarService.postsByDay()), HttpStatus.OK);
    }

    @GetMapping(value = "api/statistics/all")
    public ResponseEntity<AllStatisticsResponse> allStatistics() {
//вставить вместо true проверку, является ли user модератором
        if (globalSettingService.statisticsIsPublic() || true) {
            return new ResponseEntity<>(statisticsMapper.convertToDto(
                    statisticsService.getAllPostsOrderByTimeAsc()),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}

package application.controller;

import application.api.request.GlobalSettingRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostCommentRequest;
import application.api.request.ProfileRequest;
import application.api.response.*;
import application.exception.ApiValidationException;
import application.mapper.CalendarMapper;
import application.mapper.GlobalSettingMapper;
import application.mapper.StatisticsMapper;
import application.mapper.TagMapper;
import application.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class ApiGeneralController {

    private final InitServiceImpl initService;
    private final GlobalSettingServiceImpl globalSettingService;
    private final GlobalSettingMapper settingMapper;
    private final TagServiceImpl tagService;
    private final TagMapper tagMapper;
    private final CalendarServiceImpl calendarService;
    private final CalendarMapper calendarMapper;
    private final StatisticsServiceImpl statisticsService;
    private final StatisticsMapper statisticsMapper;
    private final PostServiceImpl postService;
    private final PostCommentServiceImpl postCommentService;
    private final RegisterServiceImpl registerService;
    private final ImageServiceImpl imageService;

    @GetMapping(value = "api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    @GetMapping(value = "api/settings")
    public ResponseEntity<GlobalSettingResponse> getSettings() {
        return new ResponseEntity<>(settingMapper.convertToDto(
                globalSettingService.getGlobalSettings()), HttpStatus.OK);
    }

    @PutMapping(value = "api/settings")
    public void setSettings(
            @Valid @RequestBody GlobalSettingRequest request, HttpSession session) {
        globalSettingService.saveGlobalSettings(settingMapper
                .convertToEntity(request), session);
    }

    @GetMapping(value = "api/tag")
    public ResponseEntity<TagsResponse> tags(
            @RequestParam(required = false) String query) {
        return new ResponseEntity<>(tagMapper.convertToDto(tagService.getTags(query),
                tagService.getWeights()), HttpStatus.OK);
    }

    @GetMapping(value = "api/calendar")
    public ResponseEntity<CalendarResponse> calendar(
            @RequestParam(required = false) int year) {
        return new ResponseEntity<>(calendarMapper.convertToDto(calendarService
                .postsByDayPerYear(year), calendarService.timeOfEarliestPost()), HttpStatus.OK);
    }

    @GetMapping(value = "api/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics(HttpSession session) {
        return new ResponseEntity<>(statisticsMapper.convertToDto(
                statisticsService.getAllPostsOrderByTimeAsc(session)), HttpStatus.OK);
    }

    @GetMapping(value = "api/statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics(HttpSession session) {
        return new ResponseEntity<>(statisticsMapper.convertToDto(
                statisticsService.getAllPostsByUserIdOrderByTimeAsc(session)), HttpStatus.OK);
    }

    @PostMapping("api/moderation")
    public ResponseEntity<ResultResponse> moderation(
            @Valid @RequestBody ModerationRequest request, HttpSession session) {
        return new ResponseEntity<>(new ResultResponse(postService
                .moderatePost(request, session)), HttpStatus.OK);
    }

    @PostMapping(value = "api/image", consumes = {"multipart/form-data"})
    public ResponseEntity<String> image(
            @RequestParam("image") MultipartFile file,
            HttpSession session) throws IOException {
        return new ResponseEntity<>(imageService.uploadImage(file, session), HttpStatus.OK);
    }

    @PostMapping(path = "api/profile/my", consumes = {"multipart/form-data"})
    public ResponseEntity<ResultResponse> profileWithPhoto(
            @RequestParam(name = "photo", required = false) MultipartFile file,
            @RequestParam(required = false) Integer removePhoto,
            @RequestParam(required = false) String password,
            @RequestParam String name,
            @RequestParam String email,
            HttpSession session) throws Exception {
        registerService.changeProfile(file, removePhoto, password, name, email, session);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping(path = "api/profile/my", consumes = {"application/json;charset=UTF-8"})
    public ResponseEntity<ResultResponse> profileWithoutPhoto(
            @Valid @RequestBody ProfileRequest request,
            HttpSession session) throws Exception {
        registerService.changeProfile(null, request.getRemovePhoto(),
                request.getPassword(), request.getName(), request.getEmail(), session);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("api/comment")
    public ResponseEntity<CommentResponse> comment(
            @Valid @RequestBody PostCommentRequest request,
            HttpSession session) throws ApiValidationException {
        return new ResponseEntity<>(new CommentResponse(postCommentService
                .addPostComment(request, session)), HttpStatus.OK);
    }
}

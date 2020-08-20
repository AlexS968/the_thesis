package application.controller;

import application.api.request.GlobalSettingRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostCommentRequest;
import application.api.request.ProfileRequest;
import application.api.response.*;
import application.exception.ApiValidationException;
import application.exception.EntityNotFoundException;
import application.exception.UserUnauthorizedException;
import application.mapper.*;
import application.model.Post;
import application.model.PostComment;
import application.model.User;
import application.service.*;
import application.service.interfaces.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

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
    private final PostServiceImpl postService;
    private final UserServiceImpl userService;
    private final PostCommentServiceImpl postCommentService;
    private final PostCommentMapper postCommentMapper;
    private final RegisterServiceImpl registerService;
    private final ImageServiceImpl imageService;

    public ApiGeneralController(InitServiceImpl initService, GlobalSettingServiceImpl globalSettingService, GlobalSettingMapper settingMapper, TagService tagService, TagMapper tagMapper, CalendarServiceImpl calendarService, CalendarMapper calendarMapper, StatisticsServiceImpl statisticsService, StatisticsMapper statisticsMapper, PostServiceImpl postService, UserServiceImpl userService, PostCommentServiceImpl postCommentService, PostCommentMapper postCommentMapper, RegisterServiceImpl registerService, ImageServiceImpl imageService) {
        this.initService = initService;
        this.globalSettingService = globalSettingService;
        this.settingMapper = settingMapper;
        this.tagService = tagService;
        this.tagMapper = tagMapper;
        this.calendarService = calendarService;
        this.calendarMapper = calendarMapper;
        this.statisticsService = statisticsService;
        this.statisticsMapper = statisticsMapper;
        this.postService = postService;
        this.userService = userService;
        this.postCommentService = postCommentService;
        this.postCommentMapper = postCommentMapper;
        this.registerService = registerService;
        this.imageService = imageService;
    }

    @GetMapping(value = "api/init")
    public ResponseEntity<InitResponse> init() {
        return initService.getInit();
    }

    @GetMapping(value = "api/settings")
    public ResponseEntity<GlobalSettingResponse> getSettings() {
        return new ResponseEntity<>(settingMapper.convertToDto(
                globalSettingService.getGlobalSettings()),
                HttpStatus.OK);
    }

    @PutMapping(value = "api/settings")
    public void setSettings(
            @Valid @RequestBody GlobalSettingRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        globalSettingService.saveGlobalSettings(
                settingMapper.convertToEntity(request), user);
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
        return new ResponseEntity<>(calendarMapper.convertToDto(
                calendarService.postsByDayPerYear(year),
                calendarService.timeOfEarliestPost()), HttpStatus.OK);
    }

    @GetMapping(value = "api/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics(HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        if (!globalSettingService.statisticsIsPublic() & !user.isModerator()) {
            throw new UserUnauthorizedException();
        }
        return new ResponseEntity<>(statisticsMapper.convertToDto(
                statisticsService.getAllPostsOrderByTimeAsc()), HttpStatus.OK);
    }

    @GetMapping(value = "api/statistics/my")
    public ResponseEntity<StatisticsResponse> myStatistics(HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        return new ResponseEntity<>(statisticsMapper.convertToDto(
                statisticsService.getAllPostsByUserIdOrderByTimeAsc(user.getId())),
                HttpStatus.OK);
    }

    @PostMapping("api/moderation")
    public ResponseEntity<ResultResponse> moderation(
            @Valid @RequestBody ModerationRequest request, HttpSession session) {
        User moderator = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        postService.moderatePost(request.getPost_id(), moderator, request.getDecision());
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("api/image")
    public ResponseEntity<String> image(@RequestParam("image") MultipartFile file,
                                        HttpSession session) throws IOException {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        return new ResponseEntity<>(imageService.uploadImage(file, user), HttpStatus.OK);
    }

    @PostMapping(path = "api/profile/my", headers = "Content-Type=multipart/form-data")
    public ResponseEntity<ResultResponse> profileWithPhoto(@RequestParam(name = "photo", required = false) MultipartFile file,
                                                           @RequestParam(required = false) Integer removePhoto,
                                                           @RequestParam(required = false) String password, @RequestParam String name,
                                                           @RequestParam String email, HttpSession session) throws Exception {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        registerService.changeProfile(file, removePhoto, password, name, email, user);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping(path = "api/profile/my", headers = "Content-Type=application/json;charset=UTF-8")
    public ResponseEntity<ResultResponse> profileWithoutPhoto(
            @Valid @RequestBody ProfileRequest request, HttpSession session)
            throws Exception {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        registerService.changeProfile(null, request.getRemovePhoto(), request.getPassword(),
                request.getName(), request.getEmail(), user);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("api/comment")
    public ResponseEntity<CommentResponse> comment(
            @Valid @RequestBody PostCommentRequest request, HttpSession session) throws ApiValidationException {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        Post post = postService.getPostByID(request.getPost_id());
        Long parentCommentId = request.getParent_id();
        PostComment parentComment = parentCommentId != null ?
                postCommentService.getCommentById(parentCommentId) : null;

        long commentId = postCommentService.addPostComment(postCommentMapper.convertToEntity(
                user, post, parentComment, parentCommentId, request.getText()));
        return new ResponseEntity<>(new CommentResponse(commentId), HttpStatus.OK);
    }
}

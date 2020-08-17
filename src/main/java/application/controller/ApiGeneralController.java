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
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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

    @Value("${upload.path}")
    private String uploadPath;

    public ApiGeneralController(InitServiceImpl initService, GlobalSettingServiceImpl globalSettingService, GlobalSettingMapper settingMapper, TagService tagService, TagMapper tagMapper, CalendarServiceImpl calendarService, CalendarMapper calendarMapper, StatisticsServiceImpl statisticsService, StatisticsMapper statisticsMapper, PostServiceImpl postService, UserServiceImpl userService, PostCommentServiceImpl postCommentService, PostCommentMapper postCommentMapper) {
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
    public String imageUpload(@RequestParam("image") MultipartFile file,
                              HttpSession session) throws IOException {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        String pathname = null;
        if (file != null) {


            String path = UUID.randomUUID().toString();
            String firstDir = path.substring(0, 7);
            String secondDir = path.substring(9, 12);
            String thirdDir = path.substring(14, 17);
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            uploadDir = new File(uploadPath + "/" + firstDir);
            uploadDir.mkdir();
            uploadDir = new File(uploadPath + "/" + firstDir + "/" + secondDir);
            uploadDir.mkdir();
            uploadDir = new File(uploadPath + "/" + firstDir + "/" + secondDir + "/" + thirdDir);
            uploadDir.mkdir();


            String resultFilename = path.substring(19) + file.getOriginalFilename();
            pathname = uploadPath + "/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + resultFilename;
            file.transferTo(new File(uploadPath + "/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + resultFilename));
        }
        return pathname;
    }

    @PostMapping("api/profile/my")
    public ResponseEntity<ResultResponse> editProfile(
            @ModelAttribute ProfileRequest request, HttpSession session) throws Exception {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        String pathDir = uploadPath + "/" + "Avatars";
        File uploadDir = new File(pathDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        //upload image
        MultipartFile file = request.getPhoto();
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        File avatar = new File(pathDir + "/" + fileName);
        file.transferTo(avatar);
        //resize image
        BufferedImage image = ImageIO.read(avatar);
        BufferedImage newImage = CaptchaMapper.resizeImage(image, 36, 36);
        ImageIO.write(newImage, "jpg", avatar);
        //update user
        user.setPhoto(fileName);
        userService.saveUser(user);
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

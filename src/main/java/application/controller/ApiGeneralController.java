package application.controller;

import application.api.request.GlobalSettingRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostCommentRequest;
import application.api.request.ProfileRequest;
import application.api.response.*;
import application.exception.ApiValidationException;
import application.service.interfaces.*;
import application.service.mapper.CalendarMapper;
import application.service.mapper.GlobalSettingMapper;
import application.service.mapper.StatisticsMapper;
import application.service.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class ApiGeneralController {
    private final CalendarService calendarService;
    private final GlobalSettingService globalSettingService;
    private final ImageService imageService;
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final RegisterService registerService;
    private final StatisticsService statisticsService;
    private final TagService tagService;
    private final CalendarMapper calendarMapper;
    private final GlobalSettingMapper settingMapper;
    private final StatisticsMapper statisticsMapper;
    private final TagMapper tagMapper;

    @Value("${blogInfo.title}")
    String title;
    @Value("${blogInfo.subtitle}")
    String subtitle;
    @Value("${blogInfo.phone}")
    String phone;
    @Value("${blogInfo.email}")
    String email;
    @Value("${blogInfo.copyright}")
    String copyright;
    @Value("${blogInfo.copyrightFrom}")
    String copyrightFrom;

    @GetMapping(value = "api/init")
    public ResponseEntity<InitResponse> init() {
        return ResponseEntity.ok(
                new InitResponse(title, subtitle, phone, email, copyright, copyrightFrom));
    }

    @GetMapping(value = "api/settings")
    public ResponseEntity<GlobalSettingResponse> getSettings() {
        return ResponseEntity.ok(settingMapper.convertToDto(globalSettingService.getGlobalSettings()));
    }

    @PutMapping(value = "api/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void setSettings(@Valid @RequestBody GlobalSettingRequest request, Principal principal) {
        globalSettingService.saveGlobalSettings(settingMapper.convertToEntity(request), principal);
    }

    @GetMapping(value = "api/tag")
    public ResponseEntity<TagsResponse> tags(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(tagMapper.convertToDto(tagService.getTags(query), tagService.getWeights()));
    }

    @GetMapping(value = "api/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(calendarMapper.convertToDto(calendarService
                .postsByDayPerYear(year), calendarService.timeOfEarliestPost()));
    }

    @GetMapping(value = "api/statistics/all")
    public ResponseEntity<StatisticsResponse> allStatistics(Principal principal) {
        return ResponseEntity.ok(statisticsMapper.convertToDto(
                statisticsService.getAllPostsOrderByTimeAsc(principal)));
    }

    @GetMapping(value = "api/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticsResponse> myStatistics(Principal principal) {
        return ResponseEntity.ok(statisticsMapper.convertToDto(
                statisticsService.getAllPostsByUserIdOrderByTimeAsc(principal)));
    }

    @PostMapping("api/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderation(
            @Valid @RequestBody ModerationRequest request, Principal principal) {
        return ResponseEntity.ok(postService.moderatePost(request, principal));
    }

    @PostMapping(value = "api/image", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<String> image(
            @RequestParam("image") MultipartFile file, Principal principal) throws IOException {
        return ResponseEntity.ok(imageService.uploadImage(file, principal));
    }

    @PostMapping(path = "api/profile/my", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> profileWithPhoto(
            @RequestParam(name = "photo", required = false) MultipartFile file,
            @RequestParam(required = false) Integer removePhoto,
            @RequestParam(required = false) String password,
            @RequestParam String name,
            @RequestParam String email,
            Principal principal) throws Exception {
        return ResponseEntity.ok(registerService
                .changeProfile(file, removePhoto, password, name, email, principal));
    }

    @PostMapping(path = "api/profile/my", consumes = {"application/json;charset=UTF-8"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> profileWithoutPhoto(
            @Valid @RequestBody ProfileRequest request, Principal principal) throws Exception {
        return ResponseEntity.ok(registerService.changeProfile(null, request.getRemovePhoto(),
                request.getPassword(), request.getName(), request.getEmail(), principal));
    }

    @PostMapping("api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentResponse> comment(
            @Valid @RequestBody PostCommentRequest request, Principal principal) throws ApiValidationException {
        return ResponseEntity.ok(postCommentService.addPostComment(request, principal));
    }
}

package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.GlobalSettingRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostCommentRequest;
import application.api.request.ProfileRequest;
import application.api.response.*;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.model.GlobalSetting;
import application.model.ModerationStatus;
import application.model.Post;
import application.repository.GlobalSettingRepository;
import application.repository.PostCommentRepository;
import application.repository.PostRepository;
import application.service.ImageServiceImpl;
import application.service.LoginServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class ApiGeneralControllerTest extends AbstractIntegrationTest {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    GlobalSettingRepository settingRepository;
    @MockBean
    ImageServiceImpl imageService;

    private MockHttpSession session;

    @Before
    public void setUp() {
        session = new MockHttpSession();
    }

    @Test
    public void shouldInitialize() throws Exception {
        InitResponse response = new InitResponse();
        response.setTitle("DevPub");
        response.setSubtitle("Program developer stories");
        response.setPhone("+7 903 666-44-55");
        response.setEmail("mail@mail.ru");
        response.setCopyright("Dmitry Sergeev");
        response.setCopyrightFrom("2005");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/init"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldGetSettings() throws Exception {
        //update repository
        GlobalSetting setting1 = settingRepository.findByCode("MULTIUSER_MODE");
        setting1.setValue("Yes");
        GlobalSetting setting2 = settingRepository.findByCode("POST_PREMODERATION");
        setting2.setValue("No");
        GlobalSetting setting3 = settingRepository.findByCode("STATISTICS_IS_PUBLIC");
        setting3.setValue("Yes");
        settingRepository.save(setting1);
        settingRepository.save(setting2);
        settingRepository.save(setting3);
        //create response
        GlobalSettingResponse response = new GlobalSettingResponse();
        response.setMultiuserMode(true);
        response.setPostPremoderation(false);
        response.setStatisticsIsPublic(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/settings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldSetSettingsIfUserIsModerator() throws Exception {
        //authenticate user with userId = 2, user is moderator
        loginService.addSessionId(session.getId(), 2);
        //create request
        GlobalSettingRequest request = new GlobalSettingRequest();
        request.setMultiuserMode(true);
        request.setPostPremoderation(false);
        request.setStatisticsIsPublic(false);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldGetAllTagsWithWeights() throws Exception {
        //create response
        TagsResponse.TagResponse[] tags = {new TagsResponse.TagResponse("java", 1.0),
                new TagsResponse.TagResponse("spring", 2.0 / 3.0)};
        TagsResponse response = new TagsResponse(tags);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldGetTagsWithWeightsBySearch() throws Exception {
        //create response
        TagsResponse.TagResponse[] tags = {new TagsResponse.TagResponse("java", 1.0)};
        TagsResponse response = new TagsResponse(tags);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                .param("query", "ja"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldFillCalendar() throws Exception {
        //create response
        Integer[] years = {2019, 2020};
        Map<String, Integer> posts = new HashMap<>();
        posts.put("2019-10-02", 1);
        CalendarResponse response = new CalendarResponse(years, posts);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/calendar")
                .param("year", "2019"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldShowAllStatistic() throws Exception {
        //authenticate user with userId = 2, user is moderator
        loginService.addSessionId(session.getId(), 2);
        //create response
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(4);
        response.setLikesCount(4);
        response.setDislikesCount(1);
        response.setViewsCount(22);
        LocalDateTime time = LocalDateTime
                .of(2019, 10, 2, 10, 23, 54);
        response.setFirstPublication(time.toEpochSecond(ZoneOffset.ofHours(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/all")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldNotShowAllStatisticAndSend401IfProhibitedAndUserUnauthenticated() throws Exception {
        //authenticate user with userId = 1, user is not moderator
        loginService.addSessionId(session.getId(), 1);
        //prohibit show statistic
        GlobalSetting setting = settingRepository.findByCode("STATISTICS_IS_PUBLIC");
        setting.setValue("No");
        settingRepository.save(setting);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/all"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void shouldShowUserStatistic() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //create response
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(2);
        response.setLikesCount(2);
        response.setDislikesCount(0);
        response.setViewsCount(11);
        LocalDateTime time = LocalDateTime //2020-08-04 10:23:54
                .of(2019, 10, 2, 10, 23, 54);
        response.setFirstPublication(time.toEpochSecond(ZoneOffset.ofHours(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/my")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldModerateIfUserIsModerator() throws Exception {
        //authenticate user with userId = 2, user is moderator
        loginService.addSessionId(session.getId(), 2);
        //create request
        ModerationRequest request = new ModerationRequest(3, "declined");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotModerateIfUserIsNotModeratorAndReturn200AndFalse() throws Exception {
        //authenticate user with userId = 1, user is not moderator
        loginService.addSessionId(session.getId(), 1);
        //create request
        ModerationRequest request = new ModerationRequest(3, "declined");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(false))));
    }

    @Test
    public void shouldUploadImageAndReturnPath() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //mock MultipartFile
        MockMultipartFile file =
                new MockMultipartFile(
                        "image",
                        "file.jpg",
                        String.valueOf(MediaType.MULTIPART_FORM_DATA),
                        "<<jpg data>>".getBytes(StandardCharsets.UTF_8));
        //mock uploadImage() from ImageServiceImpl
        Mockito.when(imageService.uploadImage(file, session)).thenReturn("path");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("path"));
    }

    @Test
    public void shouldChangeProfileWithPhoto() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //mock MultipartFile
        MockMultipartFile file =
                new MockMultipartFile(
                        "image",
                        "file.jpg",
                        String.valueOf(MediaType.MULTIPART_FORM_DATA),
                        "<<jpg data>>".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/profile/my")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
                .param("name", "Mike")
                .param("email", "mike@mail.ru")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldChangeProfileWithoutPhoto() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //create request
        ProfileRequest request = new ProfileRequest();
        //name can not be empty
        request.setName("Mike");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotChangeProfileIfInfoIsNotCorrectAndReturn200AndFalseAndCause() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //create request
        ProfileRequest request = new ProfileRequest();
        //name can not be empty
        request.setName("");
        //password can not be shorter than 6 characters
        request.setPassword("1111");
        //email should not already be registered in the database
        request.setEmail("petrov@mail.ru");
        //create response
        ApiValidationError errors = new ApiValidationError();
        errors.setName("Name is incorrect");
        errors.setEmail("User with this email already exists");
        errors.setPassword("Password is shorter than 6 characters");
        ApiError error = new ApiError(false, errors);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(error)));
    }

    @Test
    @Transactional
    public void shouldPlaceComment() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //create request
        PostCommentRequest request = new PostCommentRequest();
        request.setPost_id(2);
        request.setText("test comment, test comment, test comment");
        //create response
        CommentResponse response = new CommentResponse(
                postCommentRepository.findMaxId() + 1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldNotPlaceCommentIfTextIsTooShortAndReturn200AndFalseAndCause() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        //create request with too short comment text
        PostCommentRequest request = new PostCommentRequest();
        request.setPost_id(2);
        request.setText("test comment");
        //create response
        ApiValidationError errors = new ApiValidationError();
        errors.setText("Text is missing or too short");
        ApiError error = new ApiError(false, errors);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(error)));
    }

    @After
    public void tearDown() {
        //recover PostRepository
        Post post = postRepository.findById(3L).get();
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);
        //recover GlobalSettingRepository
        GlobalSetting setting = settingRepository.findByCode("STATISTICS_IS_PUBLIC");
        setting.setValue("Yes");
        settingRepository.save(setting);
    }
}

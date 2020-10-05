package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.GlobalSettingRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostCommentRequest;
import application.api.request.ProfileRequest;
import application.api.response.*;
import application.api.response.type.TagResponse;
import application.exception.EntNotFoundException;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.model.GlobalSetting;
import application.model.PostComment;
import application.model.User;
import application.model.enums.ModerationStatus;
import application.model.Post;
import application.model.repository.GlobalSettingRepository;
import application.model.repository.PostCommentRepository;
import application.model.repository.PostRepository;
import application.model.repository.UserRepository;
import application.service.ImageServiceImpl;
import application.service.LoginServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
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
    private GlobalSettingRepository settingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @MockBean
    ImageServiceImpl imageService;
    @MockBean
    Principal mockPrincipal;

    private final String EMAIL = "andy@mail.ru";
    private final String USER_EMAIL_FLYWAY ="ivanov@mail.ru";
    private final String MODERATOR_EMAIL_FLYWAY ="petrov@mail.ru";

    @Before
    public void setUp() {
        //create and save test user
        String NAME = "Andy";
        String PASSWORD = "password";
        boolean IS_MODERATOR = true;
        User user = new User(NAME, EMAIL, encoder.encode(PASSWORD), LocalDateTime.now(), IS_MODERATOR);
        userRepository.save(user);
    }

    @Test
    public void shouldInitialize() throws Exception {
        InitResponse response = new InitResponse("DevPub","Program developer stories",
                "+7 903 666-44-55","mail@mail.ru","Dmitry Sergeev","2005");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/init"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldGetSettings() throws Exception {
        //create response
        GlobalSettingResponse response = new GlobalSettingResponse(
                settingRepository.findByCode("MULTIUSER_MODE").get().getValue().equals("Yes"),
                settingRepository.findByCode("POST_PREMODERATION").get().getValue().equals("Yes"),
                settingRepository.findByCode("STATISTICS_IS_PUBLIC").get().getValue().equals("Yes"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/settings"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = EMAIL, authorities = { "user:moderate" })
    public void shouldSetSettingsIfUserIsModerator() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
        //create request
        GlobalSettingRequest request = new GlobalSettingRequest(
                true,true,true);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldGetAllTagsWithWeights() throws Exception {
        //create response
        TagResponse[] tags = {new TagResponse("java", 1.0),
                new TagResponse("spring", 2.0 / 3.0)};
        TagsResponse response = new TagsResponse(tags);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldGetTagsWithWeightsBySearch() throws Exception {
        //create response
        TagResponse[] tags = {new TagResponse("java", 1.0)};
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
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
        //create response
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(4);
        response.setLikeCount(4);
        response.setDislikeCount(1);
        response.setViewCount(22);
        LocalDateTime time = LocalDateTime
                .of(2019, 10, 2, 10, 23, 54);
        response.setFirstPublication(time.toEpochSecond(ZoneOffset.ofHours(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/all")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldNotShowAllStatisticAndSend401IfProhibitedAndUserIsNotModerator() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //prohibit show statistic
        GlobalSetting setting = settingRepository.findByCode("STATISTICS_IS_PUBLIC")
                .orElseThrow(EntNotFoundException::new);
        setting.setValue("No");
        settingRepository.save(setting);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/all"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = MODERATOR_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldShowAllStatisticAndSend200IfProhibitedAndUserNotModerator() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(MODERATOR_EMAIL_FLYWAY);
        //prohibit show statistic
        GlobalSetting setting = settingRepository.findByCode("STATISTICS_IS_PUBLIC")
                .orElseThrow(EntNotFoundException::new);
        setting.setValue("No");
        settingRepository.save(setting);
        //create response
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(4);
        response.setLikeCount(4);
        response.setDislikeCount(1);
        response.setViewCount(22);
        LocalDateTime time = LocalDateTime
                .of(2019, 10, 2, 10, 23, 54);
        response.setFirstPublication(time.toEpochSecond(ZoneOffset.ofHours(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/all")
        .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldShowUserStatistic() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //create response
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(2);
        response.setLikeCount(2);
        response.setDislikeCount(0);
        response.setViewCount(11);
        LocalDateTime time = LocalDateTime //2020-08-04 10:23:54
                .of(2019, 10, 2, 10, 23, 54);
        response.setFirstPublication(time.toEpochSecond(ZoneOffset.ofHours(1)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics/my")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = MODERATOR_EMAIL_FLYWAY, authorities = { "user:moderate" })
    public void shouldModerateIfUserIsModerator() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(MODERATOR_EMAIL_FLYWAY);
        //create request
        ModerationRequest request = new ModerationRequest(3, "declined");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldNotModerateIfUserIsNotModeratorAndReturn200AndFalse() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //create request
        ModerationRequest request = new ModerationRequest(3, "declined");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/moderation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(false))));
    }

    @Test
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldUploadImageAndReturnPath() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //mock MultipartFile
        MockMultipartFile file =
                new MockMultipartFile(
                        "image",
                        "file.jpg",
                        String.valueOf(MediaType.MULTIPART_FORM_DATA),
                        "<<jpg data>>".getBytes(StandardCharsets.UTF_8));
        //mock uploadImage() from ImageServiceImpl
        Mockito.when(imageService.uploadImage(file,mockPrincipal)).thenReturn("path");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/image")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("path"));
    }

    @Test
    @WithMockUser(username = EMAIL, authorities = { "user:write" })
    public void shouldChangeProfileWithPhoto() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
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
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    @WithMockUser(username = EMAIL, authorities = { "user:write" })
    public void shouldChangeProfileWithoutPhoto() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
        //create request
        ProfileRequest request = new ProfileRequest();
        //name can not be empty
        request.setName("Mike");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/profile/my")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    @WithMockUser(username = EMAIL, authorities = { "user:write" })
    public void shouldNotChangeProfileIfInfoIsNotCorrectAndReturn200AndFalseAndCause() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
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
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(error)));
    }

    @Test
    @Transactional
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldPlaceComment() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //create request
        PostCommentRequest request = new PostCommentRequest();
        request.setPostId(2);
        request.setText("test comment, test comment, test comment");
        //create response
        CommentResponse response = new CommentResponse(
                postCommentRepository.findMaxId() + 1);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL_FLYWAY, authorities = { "user:write" })
    public void shouldNotPlaceCommentIfTextIsTooShortAndReturn200AndFalseAndCause() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(USER_EMAIL_FLYWAY);
        //create request with too short comment text
        PostCommentRequest request = new PostCommentRequest();
        request.setPostId(2);
        request.setText("test comment");
        //create response
        ApiValidationError errors = new ApiValidationError();
        errors.setText("Text is missing or too short");
        ApiError error = new ApiError(false, errors);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(error)));
    }

    @After
    public void tearDown() {
        // clean user repository
        userRepository.deleteByEmail(EMAIL);
        userRepository.deleteByEmail("mike@mail.ru");
        //recover PostRepository
        Post post = postRepository.findById(3L).get();
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);
    }
}

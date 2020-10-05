package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.ChangePasswordRequest;
import application.api.request.LoginRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.request.RegisterRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.CaptchaResponse;
import application.api.response.ResultResponse;
import application.api.response.type.UserAuthCheckResponse;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.model.CaptchaCode;
import application.model.User;
import application.model.repository.CaptchaCodeRepository;
import application.model.repository.PostCommentRepository;
import application.model.repository.UserRepository;
import application.service.CaptchaServiceImpl;
import application.service.LoginServiceImpl;
import application.service.mapper.CaptchaMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ApiAuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @Autowired
    private CaptchaMapper captchaMapper;
    @Autowired
    PasswordEncoder encoder;
    @MockBean
    private CaptchaServiceImpl captchaService;
    @MockBean
    Principal mockPrincipal;

    private User user;
    private final String NAME = "Andy";
    private final String EMAIL = "andy@mail.ru";
    private final String PASSWORD = "password";
    private final boolean IS_MODERATOR = false;
    private final String NEW_EMAIL = "katrin@gmail.com";
    private CaptchaCode captchaCode;
    private final String CODE = "3332";
    private final String SECRET_CODE = "888888888888888";
    private final String HASH = "888nf1reaf72wigaqy4nihe6d8192w7vv5gio5s9mww44";

    @Before
    public void setUp() {
        //create and save test user
        user = new User(NAME, EMAIL, encoder.encode(PASSWORD), LocalDateTime.now(), IS_MODERATOR);
        user.setCode(HASH);
        userRepository.save(user);
        //clean captcha repository
        captchaCodeRepository.deleteAllOld(Timestamp.valueOf(LocalDateTime.now()));
        //set captcha
        captchaCode = new CaptchaCode(LocalDateTime.now(), CODE, SECRET_CODE);
        captchaCodeRepository.save(captchaCode);
    }

    @Test
    public void shouldReturnUserInfoIfAuthenticated() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn(EMAIL);
        //create response
        AuthenticationResponse response = new AuthenticationResponse(
                true, new UserAuthCheckResponse(user.getId(), NAME,
                null, EMAIL, IS_MODERATOR, 0, false));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldReturn200AndFalseInfoIfNotAuthenticated() throws Exception {
        //create response
        AuthenticationResponse response = new AuthenticationResponse(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

    @Test
    public void shouldGenerateCaptcha() throws Exception {
        //mock methods from CaptchaService
        Mockito.when(captchaService.captchaGenerator()).thenReturn(captchaCode);
        Mockito.when(captchaService.conversionToBase64(captchaCode)).thenReturn(
                String.valueOf(captchaCode.hashCode()));
        //create a response
        CaptchaResponse response = captchaMapper
                .convertToDto(captchaCode, captchaService.conversionToBase64(captchaCode));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/captcha"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

    @Test
    public void shouldRestorePasswordAndReturn200AndTrue_IfEmailIsCorrect() throws Exception {
        //create request with correct email
        PasswordRestoreRequest request = new PasswordRestoreRequest(EMAIL);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldReturn200AndFalse_IfEmailIsInvalid() throws Exception {
        //create request with incorrect email
        PasswordRestoreRequest request = new PasswordRestoreRequest("vittel@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(false))));
    }

    @Test
    public void shouldChangePasswordAndReturn200AndTrue_IfRequestIsCorrect() throws Exception {
        //create request with correct data
        ChangePasswordRequest request = new ChangePasswordRequest(HASH,
                "password", CODE, SECRET_CODE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotChangePasswordReturn200AndFalse_IfCaptchaIsInvalid() throws Exception {
        //create request with incorrect data (captcha is invalid)
        ChangePasswordRequest request = new ChangePasswordRequest(HASH,
                "password", "3333", SECRET_CODE);
        //create response in case of captcha error
        ApiValidationError errors = new ApiValidationError();
        errors.setCaptcha("Code from the picture is entered incorrectly");
        ApiError response = new ApiError(false, errors);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

    @Test
    public void shouldRegisterNewUserAndReturn200AndTrue_IfRequestIsCorrect() throws Exception {
        //create request with correct data
        RegisterRequest request = new RegisterRequest(NEW_EMAIL, "newPassword",
                NAME, CODE, SECRET_CODE);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotRegisterNewUserReturn200AndFalse_IfCodeIsInvalid() throws Exception {
        //create request with incorrect data
        RegisterRequest request = new RegisterRequest();
        request.setName(""); //cannot be empty
        request.setEmail("ivanov@mail.ru"); //already exists
        request.setPassword("2323"); //too short
        request.setCaptcha("5555"); //invalid
        request.setCaptchaSecret("111111111111111");
        //create response in case of errors
        ApiValidationError errors = new ApiValidationError();
        errors.setPassword("Password is shorter than 6 characters");
        errors.setEmail("User with this email already exists");
        errors.setCaptcha("Code from the picture is entered incorrectly");
        errors.setName("Name is incorrect");
        ApiError response = new ApiError(false, errors);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

    @Test
    public void shouldLogin() throws Exception {
        //create request
        LoginRequest request = new LoginRequest(EMAIL, PASSWORD);
        //create response
        AuthenticationResponse response = new AuthenticationResponse(
                true, new UserAuthCheckResponse(user.getId(), NAME,
                null, EMAIL, IS_MODERATOR, 0, false));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldNotLoginAndReturn200AndFalse_IfInfoIsInvalid() throws Exception {
        //create request with invalid password
        LoginRequest request = new LoginRequest(user.getEmail(), "12345678"); //password is incorrect

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new AuthenticationResponse(false))));
    }

    @Test
    @WithMockUser(username = EMAIL, authorities = { "user:write" })
    public void shouldLogoutWhenUserAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldLogoutAndReturnTrueAnd200EvenWhenUserUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @After
    public void tearDown() {
        // clean user repository
        userRepository.deleteByEmail(EMAIL);
        userRepository.deleteByEmail(NEW_EMAIL);
    }
}

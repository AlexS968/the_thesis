package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.*;
import application.api.response.AuthenticationResponse;
import application.api.response.CaptchaResponse;
import application.api.response.ResultResponse;
import application.api.response.type.UserAuthCheckResponse;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.service.mapper.CaptchaMapper;
import application.model.CaptchaCode;
import application.model.User;
import application.model.repository.CaptchaCodeRepository;
import application.model.repository.UserRepository;
import application.service.CaptchaServiceImpl;
import application.service.LoginServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ApiAuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaCodeRepository captchaCodeRepository;
    @MockBean
    private CaptchaServiceImpl captchaService;
    @Autowired
    private CaptchaMapper captchaMapper;

    private MockHttpSession session;

    @Before
    public void setUp() {
        session = new MockHttpSession();
        //clean captcha repository
        captchaCodeRepository.deleteAllOld(Timestamp.valueOf(LocalDateTime.now()));
    }

  /*  @Test
    public void shouldReturnUserInfoIfAuthenticated() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        User user = userRepository.findById(1L).get();
        //create response
        UserAuthCheckResponse userResponse = new UserAuthCheckResponse(
                user.getId(), user.getName(), user.getPhoto(), user.getEmail(),
                user.isModerator(), 0, false
        );
        AuthenticationResponse response = new AuthenticationResponse(true, userResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }*/

    @Test
    public void shouldReturn200AndFalseInfoIfAuthenticated() throws Exception {
        //create response
        AuthenticationResponse response = new AuthenticationResponse();
        response.setResult(false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

    @Test
    public void shouldGenerateCaptcha() throws Exception {
        CaptchaCode captchaCode = new CaptchaCode(
                LocalDateTime.now(), "2222", "5555555555");
        //mock both methods from the class CaptchaService
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
        PasswordRestoreRequest request = new PasswordRestoreRequest("ivanov@mail.ru");

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
        //set captcha
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "3332",
                "888888888888888");
        captchaCodeRepository.save(captchaCode);
        //set code to user
        User user = userRepository.findById(1L).get();
        user.setCode("888nf1reaf72wigaqy4nihe6d8192w7vv5gio5s9mww44");
        userRepository.save(user);
        //create request with correct data
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCode("888nf1reaf72wigaqy4nihe6d8192w7vv5gio5s9mww44");
        request.setPassword("999999");
        request.setCaptcha("3332");
        request.setCaptchaSecret("888888888888888");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotChangePasswordReturn200AndFalse_IfCodeIsInvalid() throws Exception {
        //set captcha
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "3332",
                "888888888888888");
        captchaCodeRepository.save(captchaCode);
        //set code to user
        User user = userRepository.findById(1L).get();
        user.setCode("888nf1reaf72wigaqy4nihe6d8192w7vv5gio5s9mww44");
        userRepository.save(user);
        //create request with correct data
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCode("888nf1reaf72wigaqy4nihe6d8192w7vv5gio5s9mww44");
        request.setPassword("999999");
        request.setCaptcha("3333"); // captcha is incorrect
        request.setCaptchaSecret("888888888888888");
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
        //set captcha
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "1111",
                "111111111111111");
        captchaCodeRepository.save(captchaCode);
        //create request with correct data
        RegisterRequest request = new RegisterRequest();
        request.setName("Andrew");
        request.setEmail("andy@mail.ru");
        request.setPassword("999999");
        request.setCaptcha("1111");
        request.setCaptchaSecret("111111111111111");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void shouldNotRegisterNewUserReturn200AndFalse_IfCodeIsInvalid() throws Exception {
        //set captcha
        CaptchaCode captchaCode = new CaptchaCode(LocalDateTime.now(), "1111",
                "111111111111111");
        captchaCodeRepository.save(captchaCode);
        //create request with correct data
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

 /*   @Test
    public void shouldLogin() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        User user = userRepository.findById(1L).get();
        LoginRequest request = new LoginRequest(user.getEmail(), user.getPassword());
        //create response
        UserAuthCheckResponse userResponse = new UserAuthCheckResponse(
                user.getId(), user.getName(), null, user.getEmail(),
                user.isModerator(), 0, false
        );
        AuthenticationResponse response = new AuthenticationResponse(true, userResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }
*/
/*
    @Test
    public void shouldNotLoginAndReturn200AndFalse_IfInfoIsInvalid() throws Exception {
        //authenticate user by request
        User user = userRepository.findById(1L).get();
        LoginRequest request = new LoginRequest(user.getEmail(), "12345678"); //password is incorrect
        loginService.userAuthentication(request, session);
        //create response in case of invalid password
        AuthenticationResponse response = new AuthenticationResponse();
        response.setResult(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(response)));
    }

    @Test
    public void shouldLogoutWhenUserAuthenticated() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }
*/

    @Test
    public void shouldLogoutAndReturnTrueAnd200EvenWhenUserUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @After
    public void tearDown() {
        //delete created user
        if (userRepository.findByEmail("andy@mail.ru").isPresent()) {
            userRepository.deleteById(userRepository
                    .findByEmail("andy@mail.ru").get().getId());
        }
    }
}

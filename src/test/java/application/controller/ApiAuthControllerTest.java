package application.controller;

import application.AbstractIntegrationTest;
import application.api.response.AuthenticationResponse;
import application.api.response.CaptchaResponse;
import application.mapper.CaptchaMapper;
import application.model.CaptchaCode;
import application.model.User;
import application.repository.UserRepository;
import application.service.CaptchaServiceImpl;
import application.service.LoginServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

public class ApiAuthControllerTest extends AbstractIntegrationTest {

    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private CaptchaServiceImpl captchaService;
    @Autowired
    private CaptchaMapper captchaMapper;

    private MockHttpSession session;

    @Before
    public void setUp() {
        session = new MockHttpSession();
    }

    @Test
    public void shouldReturnUserInfoIfAuthenticated() throws Exception {
        //authenticate user with userId = 1
        loginService.addSessionId(session.getId(), 1);
        User user = userRepository.findById(1L).get();
        //create response
        AuthenticationResponse response = new AuthenticationResponse();
        response.setResult(true);
        response.setUser();
        response.setUserId(user.getId());
        response.setUserEmail(user.getEmail());
        response.setUserName(user.getName());
        response.setUserPhoto(user.getPhoto());
        response.setUserSettings(false);
        response.setUserModeration(user.isModerator());
        response.setUserModerationCount(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/check")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(response)));
    }

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


    @After
    public void tearDown() {

    }
}

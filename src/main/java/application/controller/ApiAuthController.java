package application.controller;

import application.api.request.ChangePasswordRequest;
import application.api.request.LoginRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.request.RegisterRequest;
import application.api.response.*;
import application.exception.ApiValidationException;
import application.exception.EntityNotFoundException;
import application.exception.apierror.ApiValidationError;
import application.mapper.CaptchaMapper;
import application.mapper.UserMapper;
import application.model.User;
import application.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth")
public class ApiAuthController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final LoginServiceImpl loginService;
    private final CaptchaServiceImpl captchaService;
    private final CaptchaMapper captchaMapper;
    private final RegisterServiceImpl registerService;
    private final PasswordServiceImpl passwordService;
    private final PostServiceImpl postService;

    public ApiAuthController(UserServiceImpl userService, UserMapper userMapper, LoginServiceImpl loginService, CaptchaServiceImpl captchaService, CaptchaMapper captchaMapper, RegisterServiceImpl registerService, PasswordServiceImpl passwordService, PostServiceImpl postService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.loginService = loginService;
        this.captchaService = captchaService;
        this.captchaMapper = captchaMapper;
        this.registerService = registerService;
        this.passwordService = passwordService;
        this.postService = postService;
    }

    @GetMapping(value = "/check")
    public ResponseEntity<AuthenticationResponse> authCheck(HttpSession session) {
        Long userId = LoginServiceImpl.getSessionsId().get(session.getId());
        if (userId == null) {
            throw new ApiValidationException(new ApiValidationError(), "");
        }
        User user = userService.findUserById(userId)
                .orElseThrow(EntityNotFoundException::new);

        int moderationCount = !user.isModerator() ?
                0 : postService.getPostsForModeration(user, "new").size();
        return new ResponseEntity<>(userMapper.convertToDto(user, moderationCount),
                HttpStatus.OK);
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws Exception {
        return new ResponseEntity<>(captchaMapper.convertToDto(
                captchaService.captchaGenerator()), HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(
            @Valid @RequestBody PasswordRestoreRequest request) {
        userService.restorePassword(request.getEmail());
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(request);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        registerService.checkAndCreateUser(request.getEmail(),
                request.getName(), request.getCaptcha(), request.getCaptchaSecret(),
                request.getPassword());
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = loginService.userAuthentication(request.getEmail(),
                request.getPassword(), session.getId());
        int moderationCount = !user.isModerator() ?
                0 : postService.getPostsForModeration(user, "new").size();
        return new ResponseEntity<>(userMapper.convertToDto(user, moderationCount),
                HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logout(HttpSession session) {
        loginService.logout(session.getId());
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }
}

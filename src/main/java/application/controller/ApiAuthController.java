package application.controller;

import application.api.request.ChangePasswordRequest;
import application.api.request.LoginRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.request.RegisterRequest;
import application.api.response.*;
import application.mapper.CaptchaMapper;
import application.mapper.ChangePasswordMapper;
import application.mapper.RegisterMapper;
import application.mapper.UserMapper;
import application.service.AuthCheckServiceImpl;
import application.service.CaptchaServiceImpl;
import application.service.LoginServiceImpl;
import application.service.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth")
public class ApiAuthController {

    private final AuthCheckServiceImpl authCheckService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final LoginServiceImpl loginService;
    private final CaptchaServiceImpl captchaService;
    private final CaptchaMapper captchaMapper;
    private final RegisterMapper registerMapper;
    private final ChangePasswordMapper changePasswordMapper;

    public ApiAuthController(AuthCheckServiceImpl authCheckService, UserServiceImpl userService, UserMapper userMapper, LoginServiceImpl loginService, CaptchaServiceImpl captchaService, CaptchaMapper captchaMapper, RegisterMapper registerMapper, ChangePasswordMapper changePasswordMapper) {
        this.authCheckService = authCheckService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.loginService = loginService;
        this.captchaService = captchaService;
        this.captchaMapper = captchaMapper;
        this.registerMapper = registerMapper;
        this.changePasswordMapper = changePasswordMapper;
    }

    @GetMapping(value = "/check")
    public ResponseEntity<AuthCheckResponse> authCheck() {
        return authCheckService.getAuthCheck();
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponse> captcha() {
        return new ResponseEntity<>(captchaMapper.convertToDto(
                captchaService.captchaGenerator()), HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<PasswordRestoreResponse> restorePassword(
            @Valid @RequestBody PasswordRestoreRequest request) {
        return new ResponseEntity<>(new PasswordRestoreResponse(
                userService.checkUserByEmail(request.getEmail())), HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        return new ResponseEntity<>(changePasswordMapper.convertToDto(request),
                HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(registerMapper.convertToDto(request), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpSession session) {
        return new ResponseEntity<>(userMapper.convertToDto(
                loginService.userAuthentication(request.getEmail(),
                        request.getPassword(), session.getId())), HttpStatus.OK);
    }
}

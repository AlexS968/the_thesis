package application.controller;

import application.api.request.ChangePasswordRequest;
import application.api.request.LoginRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.request.RegisterRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.CaptchaResponse;
import application.api.response.ResultResponse;
import application.mapper.CaptchaMapper;
import application.mapper.UserMapper;
import application.model.CaptchaCode;
import application.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;
    private final LoginServiceImpl loginService;
    private final CaptchaServiceImpl captchaService;
    private final CaptchaMapper captchaMapper;
    private final RegisterServiceImpl registerService;
    private final PasswordServiceImpl passwordService;

    @GetMapping(value = "/check")
    public ResponseEntity<AuthenticationResponse> authCheck(HttpSession session) {
        return new ResponseEntity<>(userMapper.convertToDto(session), HttpStatus.OK);
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws Exception {
        CaptchaCode captchaCode = captchaService.captchaGenerator();
        return new ResponseEntity<>(captchaMapper.convertToDto(
                captchaCode, captchaService.conversionToBase64(captchaCode)), HttpStatus.OK);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(
            @Valid @RequestBody PasswordRestoreRequest request) {
        userService.restorePassword(request);
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
        registerService.createUser(request);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request, HttpSession session) {
        loginService.userAuthentication(request, session);
        return new ResponseEntity<>(userMapper.convertToDto(session), HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<ResultResponse> logout(HttpSession session) {
        loginService.logout(session);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }
}

package application.controller;

import application.api.request.ChangePasswordRequest;
import application.api.request.LoginRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.request.RegisterRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.CaptchaResponse;
import application.api.response.ResultResponse;
import application.persistence.model.CaptchaCode;
import application.service.CaptchaCodeService;
import application.service.LoginService;
import application.service.PasswordService;
import application.service.RegisterService;
import application.service.mapper.CaptchaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {
    private final CaptchaCodeService captchaCodeService;
    private final LoginService loginService;
    private final PasswordService passwordService;
    private final RegisterService registerService;
    private final CaptchaMapper captchaMapper;

    @GetMapping(value = "/check")
    public ResponseEntity<AuthenticationResponse> authCheck(Principal principal) {
        return ResponseEntity.ok(loginService.check(principal));
    }

    @GetMapping(value = "/captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws Exception {
        CaptchaCode captchaCode = captchaCodeService.captchaGenerator();
        return ResponseEntity.ok(captchaMapper.convertToDto(
                captchaCode, captchaCodeService.conversionToBase64(captchaCode)));
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse> restorePassword(
            @Valid @RequestBody PasswordRestoreRequest request) {
        return ResponseEntity.ok(passwordService.restorePassword(request));
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(passwordService.changePassword(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ResultResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(registerService.createUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> logout(Principal principal) {
        return ResponseEntity.ok(loginService.logout(principal));
    }
}

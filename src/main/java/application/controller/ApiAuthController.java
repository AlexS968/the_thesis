package application.controller;

import application.dto.request.LoginRequest;
import application.dto.request.PasswordRestoreRequest;
import application.dto.response.AuthCheckResponse;
import application.dto.response.LoginResponse;
import application.dto.response.PasswordRestoreResponse;
import application.mapper.UserMapper;
import application.service.impl.AuthCheckServiceImpl;
import application.service.impl.LoginServiceImpl;
import application.service.impl.UserServiceImpl;
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

    public ApiAuthController(AuthCheckServiceImpl authCheckService, UserServiceImpl userService, UserMapper userMapper, LoginServiceImpl loginService) {
        this.authCheckService = authCheckService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.loginService = loginService;
    }

    @GetMapping(value = "/check")
    public ResponseEntity<AuthCheckResponse> authCheck() {
        return authCheckService.getAuthCheck();
    }

    @PostMapping("/restore")
    public ResponseEntity<PasswordRestoreResponse> restorePassword(
            @Valid @RequestBody PasswordRestoreRequest request) {
        return new ResponseEntity<>(new PasswordRestoreResponse(
                userService.checkUserByEmail(request.getEmail())), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, HttpSession session) {
        return new ResponseEntity<>(userMapper.convertToDto(
                loginService.userAuthentication(request.getEmail(),
                        request.getPassword(), session.getId())), HttpStatus.OK);
    }
}

package application.service;

import application.api.request.LoginRequest;
import application.api.response.AuthenticationResponse;
import application.api.response.ResultResponse;
import application.api.response.type.UserAuthCheckResponse;
import application.model.repository.UserRepository;
import application.service.interfaces.LoginService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepository;
    private final PostServiceImpl postService;
    private final AuthenticationManager authenticationManager;

    @Bean
    public ModelMapper authModelMapper() {
        return new ModelMapper();
    }

    @Override
    public AuthenticationResponse check(Principal principal) {
        return principal != null ?
                getAuthenticationResponse(principal.getName()) :
                new AuthenticationResponse(false);
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) throws BadCredentialsException {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return getAuthenticationResponse(user.getUsername());
    }

    @Override
    public ResultResponse logout(Principal principal) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new ResultResponse(true);
    }

    private AuthenticationResponse getAuthenticationResponse(String email) {
        application.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        UserAuthCheckResponse userResponse = new UserAuthCheckResponse();
        authModelMapper().map(user, userResponse);
        userResponse.setModerationCount(!user.isModerator() ?
                0 : postService.getPostsForModeration(email, "new").size());
        return new AuthenticationResponse(true, userResponse);
    }
}

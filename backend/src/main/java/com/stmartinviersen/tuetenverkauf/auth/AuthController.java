package com.stmartinviersen.tuetenverkauf.auth;

import com.stmartinviersen.tuetenverkauf.user.model.LoginRequest;
import com.stmartinviersen.tuetenverkauf.user.model.LoginResponse;
import com.stmartinviersen.tuetenverkauf.user.model.RegisterRequest;
import com.stmartinviersen.tuetenverkauf.user.model.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register")
    public UserDTO register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @PostMapping(path = "/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse(authService.login(request));
    }
}

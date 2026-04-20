package com.auth.backend.app.controller;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;
import com.auth.backend.app.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        TokenResponse tokenResponse = authService.login(loginRequest, httpServletResponse);
        return ResponseEntity.ok(tokenResponse);

    }

    // register user
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userDto));
    }



}

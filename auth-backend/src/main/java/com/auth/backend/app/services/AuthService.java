package com.auth.backend.app.services;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.RefreshTokenRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;

public interface AuthService {

    UserDto register(UserDto userDto);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshToken(String refreshToken, RefreshTokenRequest body);
}

package com.auth.backend.app.auth.services;

import com.auth.backend.app.auth.payload.LoginRequest;
import com.auth.backend.app.auth.payload.RefreshTokenRequest;
import com.auth.backend.app.auth.payload.TokenResponse;
import com.auth.backend.app.auth.payload.UserDto;

public interface AuthService {

    UserDto register(UserDto userDto);

    TokenResponse login(LoginRequest loginRequest);

    TokenResponse refreshToken(String refreshToken, RefreshTokenRequest body);
}

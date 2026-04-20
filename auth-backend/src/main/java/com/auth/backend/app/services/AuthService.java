package com.auth.backend.app.services;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    UserDto register(UserDto userDto);

    TokenResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse);
}

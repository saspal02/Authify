package com.auth.backend.app.services;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;

public interface AuthService {

    UserDto register(UserDto userDto);

    TokenResponse login(LoginRequest loginRequest);
}

package com.auth.backend.app.services;

import com.auth.backend.app.dtos.UserDto;

public interface AuthService {

    UserDto registerUser(UserDto userDto);
}

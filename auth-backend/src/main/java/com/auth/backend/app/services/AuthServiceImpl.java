package com.auth.backend.app.services;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;
import com.auth.backend.app.entities.User;
import com.auth.backend.app.mapper.UserMapper;
import com.auth.backend.app.repositories.UserRepository;
import com.auth.backend.app.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    @Override
    public UserDto register(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);
    }

    public TokenResponse login(LoginRequest loginRequest) {

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnable()) {
            throw new DisabledException("User is disabled");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return TokenResponse.of(
                accessToken,
                "",
                jwtService.getAccessTtlSeconds(),
                userMapper.mapUserToUserDto(user)
        );
    }
}

package com.auth.backend.app.services;

import com.auth.backend.app.dtos.LoginRequest;
import com.auth.backend.app.dtos.RefreshTokenRequest;
import com.auth.backend.app.dtos.TokenResponse;
import com.auth.backend.app.dtos.UserDto;
import com.auth.backend.app.entities.RefreshToken;
import com.auth.backend.app.entities.User;
import com.auth.backend.app.mapper.UserMapper;
import com.auth.backend.app.repositories.RefreshTokenRepository;
import com.auth.backend.app.repositories.UserRepository;
import com.auth.backend.app.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public UserDto register(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userService.createUser(userDto);
    }
    
    public TokenResponse login(LoginRequest loginRequest) {

        Authentication authenticate = authenticate(loginRequest);

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.isEnable()) {
            throw new DisabledException("User is disabled");
        }

        String jti = UUID.randomUUID().toString();
        var refreshTokenDb = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenDb);


        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user,refreshTokenDb.getJti());

        return TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTtlSeconds(), userMapper.mapUserToUserDto(user));
    }


    public TokenResponse refreshToken(String refreshToken, RefreshTokenRequest body) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti
                (jti).orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException(("Refresh token expires or revoked"));

        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh token does not belong to the user");
        }

        //rotate refresh token
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();

        var newRefreshTokenOb = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenOb);
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenOb.getJti());

        return TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getAccessTtlSeconds(), userMapper.mapUserToUserDto(user));

    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }



}

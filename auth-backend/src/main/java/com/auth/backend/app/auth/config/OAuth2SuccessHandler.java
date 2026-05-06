package com.auth.backend.app.auth.config;

import com.auth.backend.app.auth.entities.Provider;
import com.auth.backend.app.auth.entities.RefreshToken;
import com.auth.backend.app.auth.entities.Role;
import com.auth.backend.app.auth.entities.User;
import com.auth.backend.app.auth.repositories.RefreshTokenRepository;
import com.auth.backend.app.auth.repositories.RoleRepository;
import com.auth.backend.app.auth.repositories.UserRepository;
import com.auth.backend.app.auth.services.impl.CookieService;
import com.auth.backend.app.auth.services.impl.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;
    private final RoleRepository roleRepository;

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestClient restClient;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndRedirectUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        logger.info("Successful authentication");
        logger.info(authentication.toString());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String registrationId = "unknown";

        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }

        logger.info("registrationId : {}", registrationId);
        logger.info("user: {}", oAuth2User.getAttributes());

        User user;

        switch (registrationId) {

            case "google" -> {

                String googleId = oAuth2User.getAttributes()
                        .getOrDefault("sub", "")
                        .toString();

                String email = oAuth2User.getAttributes()
                        .getOrDefault("email", "")
                        .toString();

                String name = oAuth2User.getAttributes()
                        .getOrDefault("name", "")
                        .toString();

                String picture = oAuth2User.getAttributes()
                        .getOrDefault("picture", "")
                        .toString();


                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(picture)
                        .enable(true)
                        .provider(Provider.GOOGLE)
                        .providerId(googleId)
                        .build();

                user = userRepository.findByEmail(email)
                        .orElseGet(() -> userRepository.save(newUser));

                Role role = roleRepository.findByName("ROLE_" + AppConstants.GUEST_ROLE).orElse(null);

                if (user.getRoles() == null) {
                    user.setRoles(new HashSet<>());
                }

                if (role != null) {
                    user.getRoles().add(role);
                }
            }

            case "github" -> {

                String name = oAuth2User.getAttributes()
                        .getOrDefault("login", "")
                        .toString();

                String githubId = oAuth2User.getAttributes()
                        .getOrDefault("id", "")
                        .toString();

                String image = oAuth2User.getAttributes()
                        .getOrDefault("avatar_url", "")
                        .toString();

                String email =
                        (String) oAuth2User.getAttributes().get("email");

                if (email == null || email.isBlank()) {

                    OAuth2AuthenticationToken token =
                            (OAuth2AuthenticationToken) authentication;

                    OAuth2AuthorizedClient client =
                            authorizedClientService.loadAuthorizedClient(
                                    token.getAuthorizedClientRegistrationId(),
                                    token.getName()
                            );

                    if (client != null) {

                        String accessToken =
                                client.getAccessToken().getTokenValue();

                        List<Map<String, Object>> emails =
                                restClient.get()
                                        .uri("https://api.github.com/user/emails")
                                        .headers(headers ->
                                                headers.setBearerAuth(accessToken))
                                        .retrieve()
                                        .body(new ParameterizedTypeReference<
                                                List<Map<String, Object>>>() {
                                        });

                        if (emails != null) {
                            email = emails.stream()
                                    .filter(e ->
                                            Boolean.TRUE.equals(e.get("primary")))
                                    .filter(e ->
                                            Boolean.TRUE.equals(e.get("verified")))
                                    .map(e -> e.get("email").toString())
                                    .findFirst()
                                    .orElse(null);
                        }
                    }
                }



                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(image)
                        .enable(true)
                        .provider(Provider.GITHUB)
                        .providerId(githubId)
                        .build();




                user = userRepository.findByEmail(email)
                        .orElseGet(() -> userRepository.save(newUser));

                Role role = roleRepository.findByName("ROLE_" + AppConstants.GUEST_ROLE).orElse(null);

                if (user.getRoles() == null) {
                    user.setRoles(new HashSet<>());
                }

                if (role != null) {
                    user.getRoles().add(role);
                }
            }

            default -> throw new RuntimeException("Invalid Provider");
        }

        String jti = UUID.randomUUID().toString();

        RefreshToken refreshTokenOb = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(
                        Instant.now()
                                .plusSeconds(jwtService.getRefreshTtlSeconds())
                )
                .build();

        refreshTokenRepository.save(refreshTokenOb);

        String refreshToken =
                jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        cookieService.attachRefreshCookie(
                response,
                refreshToken,
                (int) jwtService.getRefreshTtlSeconds()
        );

        response.sendRedirect(frontEndRedirectUrl);
    }
}
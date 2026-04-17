package com.auth.backend.app.dtos;

public record LoginRequest(
        String email,
        String password
) {

}

package com.auth.backend.app.auth.payload;

public record LoginRequest(
        String email,
        String password
) {

}

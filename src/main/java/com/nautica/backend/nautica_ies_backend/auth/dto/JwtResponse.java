// JwtResponse.java
package com.nautica.backend.nautica_ies_backend.auth.dto;

public record JwtResponse(String accessToken, String refreshToken, String tokenType) {
    public JwtResponse(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, "Bearer");
    }
}

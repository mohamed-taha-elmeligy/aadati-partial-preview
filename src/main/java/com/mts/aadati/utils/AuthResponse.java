package com.mts.aadati.utils;

import lombok.*;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Getter
@ToString
@Builder
@EqualsAndHashCode
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;


    private static final String TOKEN_TYPE = "Bearer";

    public String getTokenType() {
        return TOKEN_TYPE;
    }
}
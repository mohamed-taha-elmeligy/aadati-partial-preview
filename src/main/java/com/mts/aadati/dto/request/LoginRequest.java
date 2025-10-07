package com.mts.aadati.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * © 2025 Mohamed Taha
 * This file is part of the MTS Aadati Application.
 */

@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}

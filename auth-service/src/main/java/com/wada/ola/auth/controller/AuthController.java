package com.wada.ola.auth.controller;

import com.wada.ola.auth.dto.AuthResponse;
import com.wada.ola.auth.dto.LoginRequest;
import com.wada.ola.auth.dto.RegisterRequest;
import com.wada.ola.auth.service.AuthService;
import com.wada.ola.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(request)));
    }
}


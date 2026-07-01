package com.wada.ola.auth.controller;

import com.wada.ola.auth.dto.AuthResponse;
import com.wada.ola.auth.dto.LoginRequest;
import com.wada.ola.auth.dto.MfaRequiredResponse;
import com.wada.ola.auth.dto.OtpVerifyRequest;
import com.wada.ola.auth.dto.RegisterRequest;
import com.wada.ola.auth.dto.UserResponse;
import com.wada.ola.auth.service.AuthService;
import com.wada.ola.common.annotation.Audited;
import com.wada.ola.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /** Step 1 — validates credentials, generates OTP, returns mfaSessionId (no JWT). */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MfaRequiredResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("OTP sent", authService.login(request)));
    }

    /** Step 2 — validates OTP, issues JWT. */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.verifyOtp(request)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(authService.getByUsername(userDetails.getUsername())));
    }

    /** Any authenticated user can list users */
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers() {
        return ResponseEntity.ok(ApiResponse.ok(authService.listUsers()));
    }

    /** Any authenticated user can create a user — role hierarchy enforced in service */
    @PostMapping("/admin/users")
    @Audited(action = "USER_CREATE", targetTable = "users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody RegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String callerRole = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");
        return ResponseEntity.ok(ApiResponse.ok("User created", authService.createUser(request, callerRole)));
    }

    /** Only ADMIN or CHIEF can change roles or reassign command */
    @PatchMapping("/admin/users/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHIEF')")
    @Audited(action = "USER_ROLE_CHANGE", targetTable = "users")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id,
                                                                 @RequestBody Map<String, Object> body) {
        String role = body.containsKey("role") ? (String) body.get("role") : null;
        boolean commandIdProvided = body.containsKey("commandId");
        Long commandId = commandIdProvided && body.get("commandId") != null
                ? Long.valueOf(body.get("commandId").toString()) : null;
        return ResponseEntity.ok(ApiResponse.ok("User updated",
                authService.updateUser(id, role, commandId, commandIdProvided)));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid credentials"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLocked(LockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /** Only ADMIN or CHIEF can delete users */
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHIEF')")
    @Audited(action = "USER_DELETE", targetTable = "users")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        authService.softDeleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok("User deleted", null));
    }
}

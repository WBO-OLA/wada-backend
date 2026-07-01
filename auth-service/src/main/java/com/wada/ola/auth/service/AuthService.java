package com.wada.ola.auth.service;

import com.wada.ola.auth.dto.AuthResponse;
import com.wada.ola.auth.dto.LoginRequest;
import com.wada.ola.auth.dto.MfaRequiredResponse;
import com.wada.ola.auth.dto.OtpVerifyRequest;
import com.wada.ola.auth.dto.RegisterRequest;
import com.wada.ola.auth.dto.UserResponse;
import com.wada.ola.auth.entity.User;
import com.wada.ola.auth.repository.UserRepository;
import com.wada.ola.auth.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final Map<String, Integer> ROLE_LEVELS = Map.of(
        "USER", 1, "MANAGER", 2, "CHIEF", 3, "ADMIN", 4
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Value("${mfa.otp-hint-enabled:true}")
    private boolean otpHintEnabled;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
    }

    /** Public registration — always creates USER role regardless of request body */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameAndDeletedFalse(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getCommandId());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getCommandId());
    }

    /**
     * Step 1 of MFA login: validates credentials, generates OTP, returns session ID.
     * JWT is NOT issued here — only after OTP verification.
     */
    public MfaRequiredResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsernameAndDeletedFalse(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpService.OtpEntry entry = otpService.generate(user.getUsername());

        // In production set mfa.otp-hint-enabled=false and deliver otp via email/SMS instead
        log.info("[MFA] OTP for '{}': {}", user.getUsername(), entry.otp());
        String hint = otpHintEnabled ? entry.otp() : null;

        return new MfaRequiredResponse(entry.sessionId(), hint);
    }

    /**
     * Step 2 of MFA login: validates OTP session, issues JWT on success.
     */
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        String username = otpService.verify(request.getMfaSessionId(), request.getOtp());
        if (username == null) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }

        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getCommandId());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getCommandId());
    }

    /** Authenticated user creates another user — enforces role hierarchy */
    public UserResponse createUser(RegisterRequest request, String callerRole) {
        String requestedRole = request.getRole() != null ? request.getRole().toUpperCase() : "USER";
        int callerLevel = ROLE_LEVELS.getOrDefault(callerRole, 0);
        int requestedLevel = ROLE_LEVELS.getOrDefault(requestedRole, 0);

        if (requestedLevel > callerLevel) {
            throw new IllegalArgumentException(
                "Cannot create a user with role higher than your own (" + callerRole + ")");
        }

        if (userRepository.existsByUsernameAndDeletedFalse(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(requestedRole));
        user.setCommandId(request.getCommandId());
        userRepository.save(user);

        return toResponse(user);
    }

    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findByDeletedFalseOrderByCreatedAtDesc()
                .stream().map(this::toResponse).toList();
    }

    public UserResponse updateUser(Long id, String role, Long commandId, boolean commandIdProvided) {
        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (role != null && !role.isBlank()) {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        }
        if (commandIdProvided) {
            user.setCommandId(commandId);
        }
        userRepository.save(user);
        return toResponse(user);
    }

    public UserResponse updateRole(Long id, String role) {
        return updateUser(id, role, null, false);
    }

    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRole().name(), user.getCommandId(), user.getCreatedAt());
    }
}

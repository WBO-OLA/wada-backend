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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    @Value("${mfa.login-max-attempts:5}")
    private int maxLoginAttempts;

    @Value("${mfa.lock-duration-minutes:15}")
    private int lockDurationMinutes;

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
     * Step 1 of MFA login: validates credentials, enforces lockout, generates OTP.
     * JWT is NOT issued here — only after OTP verification.
     */
    public MfaRequiredResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameAndDeletedFalse(request.getUsername()).orElse(null);

        // Reject immediately if the account is currently locked
        if (user != null && user.isAccountLocked()) {
            long minutesLeft = Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes() + 1;
            throw new LockedException(
                "Account is temporarily locked. Try again in " + minutesLeft + " minute(s).");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            if (user != null) {
                int attempts = user.getFailedLoginAttempts() + 1;
                if (attempts >= maxLoginAttempts) {
                    user.setFailedLoginAttempts(0);
                    user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                    userRepository.save(user);
                    log.warn("[AUTH] Account '{}' locked after {} failed password attempts", user.getUsername(), attempts);
                    throw new LockedException(
                        "Too many failed attempts. Account locked for " + lockDurationMinutes + " minutes.");
                }
                user.setFailedLoginAttempts(attempts);
                userRepository.save(user);
                log.warn("[AUTH] Failed password attempt {}/{} for '{}'", attempts, maxLoginAttempts, user.getUsername());
            }
            throw e;
        }

        // Successful password auth — reset counter
        if (user != null && (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null)) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        }

        OtpService.OtpEntry entry = otpService.generate(user.getUsername());
        log.info("[MFA] OTP for '{}': {}", user.getUsername(), entry.otp());
        String hint = otpHintEnabled ? entry.otp() : null;
        return new MfaRequiredResponse(entry.sessionId(), hint);
    }

    /**
     * Step 2 of MFA login: validates OTP, issues JWT on success.
     * Locks the account if max OTP attempts are exceeded.
     */
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        OtpVerifyResult result = otpService.verify(request.getMfaSessionId(), request.getOtp());

        if (!result.success()) {
            switch (result.failReason()) {
                case MAX_ATTEMPTS -> {
                    if (result.username() != null) {
                        userRepository.findByUsernameAndDeletedFalse(result.username()).ifPresent(u -> {
                            u.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
                            u.setFailedLoginAttempts(0);
                            userRepository.save(u);
                            log.warn("[MFA] Account '{}' locked after max OTP attempts", u.getUsername());
                        });
                    }
                    throw new LockedException(
                        "Too many incorrect codes. Account locked for " + lockDurationMinutes + " minutes.");
                }
                case EXPIRED ->
                    throw new IllegalArgumentException("Verification code has expired. Please sign in again.");
                default ->
                    throw new IllegalArgumentException("Invalid verification code. Please try again.");
            }
        }

        User user = userRepository.findByUsernameAndDeletedFalse(result.username())
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

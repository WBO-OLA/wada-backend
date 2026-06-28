package com.wada.ola.auth.service;

import com.wada.ola.auth.dto.AuthResponse;
import com.wada.ola.auth.dto.LoginRequest;
import com.wada.ola.auth.dto.RegisterRequest;
import com.wada.ola.auth.dto.UserResponse;
import com.wada.ola.auth.entity.User;
import com.wada.ola.auth.repository.UserRepository;
import com.wada.ola.auth.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private static final Map<String, Integer> ROLE_LEVELS = Map.of(
        "USER", 1, "MANAGER", 2, "CHIEF", 3, "ADMIN", 4
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsernameAndDeletedFalse(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getCommandId());
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getCommandId());
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

    public UserResponse updateRole(Long id, String role) {
        User user = userRepository.findById(id)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
        return toResponse(user);
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

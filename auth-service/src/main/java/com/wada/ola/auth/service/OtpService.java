package com.wada.ola.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final long OTP_VALID_SECONDS = 300; // 5 minutes

    private final Map<String, OtpSession> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public record OtpEntry(String sessionId, String otp) {}

    public OtpEntry generate(String username) {
        // Remove any prior pending session for this user before creating a new one
        sessions.values().removeIf(s -> s.getUsername().equals(username));

        String sessionId = UUID.randomUUID().toString();
        String otp = String.format("%06d", random.nextInt(1_000_000));
        Instant expiresAt = Instant.now().plusSeconds(OTP_VALID_SECONDS);
        sessions.put(sessionId, new OtpSession(username, otp, expiresAt));
        log.info("[MFA] OTP generated for user '{}' — session {}", username, sessionId);
        return new OtpEntry(sessionId, otp);
    }

    /**
     * Returns the username if sessionId + otp are valid and not expired; null otherwise.
     * Always consumes the session on success.
     */
    public String verify(String sessionId, String otp) {
        OtpSession session = sessions.get(sessionId);
        if (session == null) {
            log.warn("[MFA] verify failed — unknown session {}", sessionId);
            return null;
        }
        if (session.isExpired()) {
            sessions.remove(sessionId);
            log.warn("[MFA] verify failed — session {} expired", sessionId);
            return null;
        }
        if (!session.getOtp().equals(otp)) {
            log.warn("[MFA] verify failed — wrong OTP for session {}", sessionId);
            return null;
        }
        sessions.remove(sessionId);
        log.info("[MFA] OTP verified for user '{}'", session.getUsername());
        return session.getUsername();
    }

    @Scheduled(fixedDelay = 60_000)
    void cleanExpired() {
        sessions.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}

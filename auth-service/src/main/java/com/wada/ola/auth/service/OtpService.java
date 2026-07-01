package com.wada.ola.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${mfa.otp-ttl-seconds:60}")
    private long otpTtlSeconds;

    @Value("${mfa.otp-max-attempts:3}")
    private int maxOtpAttempts;

    private final Map<String, OtpSession> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public record OtpEntry(String sessionId, String otp) {}

    public OtpEntry generate(String username) {
        sessions.values().removeIf(s -> s.getUsername().equals(username));
        String sessionId = UUID.randomUUID().toString();
        String otp = String.format("%06d", random.nextInt(1_000_000));
        Instant expiresAt = Instant.now().plusSeconds(otpTtlSeconds);
        sessions.put(sessionId, new OtpSession(username, otp, expiresAt, maxOtpAttempts));
        log.info("[MFA] OTP generated for '{}' — session {} (expires in {}s)", username, sessionId, otpTtlSeconds);
        return new OtpEntry(sessionId, otp);
    }

    /**
     * Validates the OTP. Returns a result describing success, invalid code,
     * expiry, or max-attempts exceeded (which triggers an account lock upstream).
     */
    public OtpVerifyResult verify(String sessionId, String otp) {
        OtpSession session = sessions.get(sessionId);
        if (session == null) {
            log.warn("[MFA] verify failed — unknown session {}", sessionId);
            return OtpVerifyResult.fail(null, OtpVerifyResult.FailReason.INVALID);
        }
        if (session.isExpired()) {
            sessions.remove(sessionId);
            log.warn("[MFA] verify failed — session expired for '{}'", session.getUsername());
            return OtpVerifyResult.fail(session.getUsername(), OtpVerifyResult.FailReason.EXPIRED);
        }
        if (!session.getOtp().equals(otp)) {
            boolean maxReached = session.incrementAndCheckMax();
            if (maxReached) {
                sessions.remove(sessionId);
                log.warn("[MFA] max OTP attempts reached for '{}'", session.getUsername());
                return OtpVerifyResult.fail(session.getUsername(), OtpVerifyResult.FailReason.MAX_ATTEMPTS);
            }
            log.warn("[MFA] wrong OTP for session {}", sessionId);
            return OtpVerifyResult.fail(session.getUsername(), OtpVerifyResult.FailReason.INVALID);
        }
        sessions.remove(sessionId);
        log.info("[MFA] OTP verified for '{}'", session.getUsername());
        return OtpVerifyResult.success(session.getUsername());
    }

    @Scheduled(fixedDelay = 60_000)
    void cleanExpired() {
        sessions.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}

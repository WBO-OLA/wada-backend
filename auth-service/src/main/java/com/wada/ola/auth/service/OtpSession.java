package com.wada.ola.auth.service;

import java.time.Instant;

class OtpSession {
    private final String username;
    private final String otp;
    private final Instant expiresAt;
    private final int maxAttempts;
    private int failedAttempts = 0;

    OtpSession(String username, String otp, Instant expiresAt, int maxAttempts) {
        this.username = username;
        this.otp = otp;
        this.expiresAt = expiresAt;
        this.maxAttempts = maxAttempts;
    }

    String getUsername() { return username; }
    String getOtp()      { return otp; }

    boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /** Increments counter and returns true when max attempts is reached. */
    boolean incrementAndCheckMax() {
        return ++failedAttempts >= maxAttempts;
    }
}

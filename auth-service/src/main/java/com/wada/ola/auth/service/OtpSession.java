package com.wada.ola.auth.service;

import java.time.Instant;

class OtpSession {
    private final String username;
    private final String otp;
    private final Instant expiresAt;

    OtpSession(String username, String otp, Instant expiresAt) {
        this.username = username;
        this.otp = otp;
        this.expiresAt = expiresAt;
    }

    String getUsername() { return username; }
    String getOtp()      { return otp; }

    boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}

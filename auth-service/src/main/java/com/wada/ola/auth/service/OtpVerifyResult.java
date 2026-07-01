package com.wada.ola.auth.service;

public record OtpVerifyResult(boolean success, String username, FailReason failReason) {

    public enum FailReason { INVALID, EXPIRED, MAX_ATTEMPTS }

    public static OtpVerifyResult success(String username) {
        return new OtpVerifyResult(true, username, null);
    }

    public static OtpVerifyResult fail(String username, FailReason reason) {
        return new OtpVerifyResult(false, username, reason);
    }
}

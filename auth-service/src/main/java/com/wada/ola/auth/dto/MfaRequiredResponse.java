package com.wada.ola.auth.dto;

public class MfaRequiredResponse {
    private final boolean mfaRequired = true;
    private final String mfaSessionId;
    private final String otpHint; // null in production; set in dev so the UI can display it

    public MfaRequiredResponse(String mfaSessionId, String otpHint) {
        this.mfaSessionId = mfaSessionId;
        this.otpHint = otpHint;
    }

    public boolean isMfaRequired() { return mfaRequired; }
    public String getMfaSessionId() { return mfaSessionId; }
    public String getOtpHint() { return otpHint; }
}

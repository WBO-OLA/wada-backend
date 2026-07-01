package com.wada.ola.auth.dto;

public class MfaRequiredResponse {
    private final boolean mfaRequired = true;
    private final String mfaSessionId;
    private final String otpHint;      // null in production
    private final String emailMasked;  // e.g. "m***@gmail.com" — always shown

    public MfaRequiredResponse(String mfaSessionId, String otpHint, String emailMasked) {
        this.mfaSessionId = mfaSessionId;
        this.otpHint = otpHint;
        this.emailMasked = emailMasked;
    }

    public boolean isMfaRequired()  { return mfaRequired; }
    public String getMfaSessionId() { return mfaSessionId; }
    public String getOtpHint()      { return otpHint; }
    public String getEmailMasked()  { return emailMasked; }
}

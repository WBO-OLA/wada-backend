package com.wada.ola.auth.dto;

public class OtpVerifyRequest {
    private String mfaSessionId;
    private String otp;

    public String getMfaSessionId() { return mfaSessionId; }
    public void setMfaSessionId(String mfaSessionId) { this.mfaSessionId = mfaSessionId; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}

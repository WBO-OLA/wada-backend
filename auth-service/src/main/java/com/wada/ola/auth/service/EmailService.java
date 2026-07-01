package com.wada.ola.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${mfa.otp-ttl-seconds:60}")
    private long otpTtlSeconds;

    public boolean isConfigured() {
        return mailSender != null && !fromAddress.isBlank();
    }

    /**
     * Sends the OTP to the user's registered email.
     * Returns true on success, false when mail is not configured.
     * Throws RuntimeException on delivery failure so AuthService can decide whether to abort.
     */
    public boolean sendOtp(String toEmail, String username, String otp) {
        if (!isConfigured()) {
            log.warn("[EMAIL] SMTP not configured — OTP for '{}' not emailed (hint enabled in dev)", username);
            return false;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject("MOMS — Your Verification Code");
            helper.setText(buildHtml(username, otp), true);
            mailSender.send(msg);
            log.info("[EMAIL] OTP sent to {} for user '{}'", mask(toEmail), username);
            return true;
        } catch (MessagingException e) {
            log.error("[EMAIL] Failed to send OTP to {} for '{}': {}", mask(toEmail), username, e.getMessage());
            throw new RuntimeException("Failed to send verification code. Please try again later.");
        }
    }

    /** Returns e.g. "m***@gmail.com" */
    public String mask(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }

    private String buildHtml(String username, String otp) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
            <body style="margin:0;padding:0;background:#f1f5f9;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 16px;">
                <tr><td align="center">
                  <table width="480" cellpadding="0" cellspacing="0" style="border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,.08);">

                    <!-- Header -->
                    <tr>
                      <td style="background:#0f172a;padding:28px 32px;text-align:center;">
                        <p style="margin:0;color:#fbbf24;font-size:22px;font-weight:bold;letter-spacing:2px;">MOMS</p>
                        <p style="margin:4px 0 0;color:#94a3b8;font-size:13px;">Military Organisation Management System</p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="background:#ffffff;padding:36px 32px;">
                        <p style="margin:0 0 8px;color:#374151;font-size:15px;">Hello <strong>%s</strong>,</p>
                        <p style="margin:0 0 24px;color:#6b7280;font-size:14px;line-height:1.6;">
                          Use the code below to complete your sign-in. It expires in <strong>%d seconds</strong>.
                        </p>

                        <!-- OTP box -->
                        <div style="background:#f8fafc;border:2px dashed #e2e8f0;border-radius:10px;padding:28px;text-align:center;margin:0 0 24px;">
                          <span style="font-size:40px;font-weight:bold;letter-spacing:14px;color:#0f172a;font-family:'Courier New',monospace;">%s</span>
                        </div>

                        <p style="margin:0 0 8px;color:#9ca3af;font-size:13px;line-height:1.6;">
                          Never share this code with anyone. MOMS staff will never ask for it.
                        </p>
                        <p style="margin:0;color:#9ca3af;font-size:13px;">
                          If you did not attempt to sign in, please contact your system administrator immediately.
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#f8fafc;padding:16px 32px;text-align:center;border-top:1px solid #e2e8f0;">
                        <p style="margin:0;color:#cbd5e1;font-size:12px;">
                          MOMS v1.0 &nbsp;·&nbsp; Authorised Personnel Only &nbsp;·&nbsp; All activity is logged
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(username, otpTtlSeconds, otp);
    }
}

package com.example.webchat.service;

import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base32;

@Slf4j
@Service
public class TwoFactorAuthService {

    public boolean verifyCode(String secretKey, int code) {
        Totp totp = new Totp(secretKey);
        return totp.verify(String.valueOf(code));
    }

    public String generateQRCode(String secretKey, String accountName, String issuer, String filePath) {
        try {
            String qrCodeData = "otpauth://totp/" + issuer + ":" + accountName + "?secret=" + secretKey + "&issuer=" + issuer;
            QRCodeGenerator.generateQRCode(qrCodeData, filePath);
            return "QR Code generated successfully at: " + filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR Code", e);
        }
    }

    public String generateCode(String salt) {
        try {
            if (salt == null || salt.isEmpty()) {
                throw new IllegalArgumentException("Salt cannot be null or empty");
            }

            // Validate Base32 format
            if (!salt.matches("^[A-Z2-7=]+$")) {
                throw new IllegalArgumentException("Invalid Base32-encoded salt: " + salt);
            }

            Totp totp = new Totp(salt);
            return totp.now();
        } catch (IllegalArgumentException e) {
            log.error("Failed to generate two-factor code: " + e.getMessage(), e);
            throw new RuntimeException("Invalid salt for two-factor authentication", e);
        }
    }

    public String generateSecretKey() {
        Base32 base32 = new Base32();
        byte[] randomBytes = new byte[10]; // 10 bytes = 80 bits, suitable for Base32
        new java.security.SecureRandom().nextBytes(randomBytes);
        return base32.encodeToString(randomBytes);
    }
}

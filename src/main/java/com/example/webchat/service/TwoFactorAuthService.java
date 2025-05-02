package com.example.webchat.service;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;

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
}

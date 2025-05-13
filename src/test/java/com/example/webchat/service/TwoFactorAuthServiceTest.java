package com.example.webchat.service;

import org.jboss.aerogear.security.otp.Totp;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class TwoFactorAuthServiceTest {

    @Test
    void testGenerateQRCode() {
        TwoFactorAuthService authService = new TwoFactorAuthService();

        // Example data
        String secretKey = "JBSWY3DPEHPK3PXP"; // Replace with a generated key
        String accountName = "user@example.com";
        String issuer = "MyApp";
        String filePath = "qrcode.png";

        // Generate QR Code
        String result = authService.generateQRCode(secretKey, accountName, issuer, filePath);
        System.out.println(result);
    }


    @Test
    void testVerifyCode() {
        TwoFactorAuthService authService = new TwoFactorAuthService();

        // Example data
        String secretKey = "JBSWY3DPEHPK3PXP"; // Same key used for QR code
        Totp totp = new Totp(secretKey);
        int userCode = Integer.parseInt(totp.now()); // Generate the current TOTP code
        System.out.println("Generated TOTP code: " + userCode);
        // Verify the code
        boolean isValid = authService.verifyCode(secretKey, userCode);
        System.out.println("Is the code valid? " + isValid);

        assertTrue(isValid, "The TOTP code should be valid.");
    }

    @Test
    void testGenerateQRCodeToFile() throws Exception {

        String qrCodeData = "otpauth://totp/user code";
        String filePath = "QRCode.png";

        // Act
        QRCodeGenerator.generateQRCodeToFile(qrCodeData, filePath);

        // Assert
        File qrCodeFile = new File(filePath);
        assertTrue(qrCodeFile.exists(), "QR Code file should exist");
        assertTrue(Files.size(qrCodeFile.toPath()) > 0, "QR Code file should not be empty");

        // Cleanup
        // qrCodeFile.delete();
    }
}
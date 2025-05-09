package com.example.webchat.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailNotificationServiceTest {


    @Test
    void testSendQrCodeEmail() {
        // Arrange
        JavaMailSender mockMailSender = Mockito.mock(JavaMailSender.class);
        EmailNotificationService emailService = new EmailNotificationService(mockMailSender);

        String to = "test@example.com";
        String subject = "Your QR Code";
        String qrCode = "QR_CODE_PLACEHOLDER"; // Replace with actual QR code generation logic
        String body = "Here is your QR code: " + qrCode;

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<MimeMessagePreparator> messageCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        verify(mockMailSender, times(1)).send(messageCaptor.capture());

        MimeMessagePreparator capturedMessage = messageCaptor.getValue();
        assertTrue(capturedMessage != null, "MimeMessagePreparator should not be null");
    }

    @Test
    void testSendQrCodeEmailWithGenerateQRCode() throws Exception {
        // Arrange
        JavaMailSender mockMailSender = Mockito.mock(JavaMailSender.class);
        EmailNotificationService emailService = new EmailNotificationService(mockMailSender);

        String to = "test@example.com";
        String subject = "Your QR Code";
        String qrCode = QRCodeGenerator.generateQRCode("Sample QR Code Data");
        String body = "Here is your QR code: " + qrCode;

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<MimeMessagePreparator> messageCaptor = ArgumentCaptor.forClass(MimeMessagePreparator.class);
        verify(mockMailSender, times(1)).send(messageCaptor.capture());

        MimeMessagePreparator capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage, "MimeMessagePreparator should not be null");
    }


}
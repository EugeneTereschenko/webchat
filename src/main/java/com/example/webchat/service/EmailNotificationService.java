package com.example.webchat.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);
        };
        mailSender.send(preparator);
    }

    public void sendEmailWithAttachment(String to, String subject, String body, String qrCodeBase64) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        // Decode the Base64 QR code and attach it as a PNG file
        byte[] qrCodeBytes = Base64.getDecoder().decode(qrCodeBase64);
        helper.addAttachment("QRCode.png", new ByteArrayDataSource(qrCodeBytes, "image/png"));

        mailSender.send(message);
    }
}

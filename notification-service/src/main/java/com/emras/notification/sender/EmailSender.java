package com.emras.notification.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Sends emails via Spring Mail.
 *
 * Local dev: configure spring.mail.* to use Mailtrap or Gmail
 * or just use the console logger (mail.enabled=false).
 *
 * Production: configure real SMTP (SendGrid, AWS SES, etc.)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    public boolean send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@emras.com.bd");

            mailSender.send(message);
            log.info("Email sent to: {} subject: {}", to, subject);
            return true;

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
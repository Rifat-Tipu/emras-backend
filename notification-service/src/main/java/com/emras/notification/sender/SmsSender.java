package com.emras.notification.sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SMS sender — simulated locally.
 *
 * Production: integrate with Bangladesh SMS gateways:
 *  - SSL Wireless (https://sslwireless.com/)
 *  - Infobip Bangladesh
 *  - Twilio (international)
 *
 * Local: just logs the SMS to console.
 */
@Slf4j
@Component
public class SmsSender {

    public boolean send(String phoneNumber, String message) {
        // Simulate SMS sending in local dev
        log.info("━━━ SMS SIMULATION ━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("To:      {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        return true;
    }
}
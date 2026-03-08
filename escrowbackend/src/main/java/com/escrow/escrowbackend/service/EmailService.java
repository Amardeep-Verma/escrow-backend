package com.escrow.escrowbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ===============================
    // GENERIC EMAIL METHOD
    // ===============================
    public void sendEmail(String to, String subject, String body) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            System.out.println("✅ Email sent to: " + to);

        } catch (Exception e) {
            // Never break API if email fails
            System.out.println("❌ Email failed: " + e.getMessage());
        }
    }


    // ===============================
    // ESCROW CREATED EMAIL
    // ===============================
    public void sendEscrowCreatedEmail(String toEmail, String productName, Double amount) {

        String subject = "New Escrow Created";

        String body =
                "Hello,\n\n" +
                        "A new escrow has been created.\n\n" +
                        "Product: " + productName + "\n" +
                        "Amount: ₹" + amount + "\n\n" +
                        "Please login to your dashboard for more details.\n\n" +
                        "Escrow Platform";

        sendEmail(toEmail, subject, body);
    }
}
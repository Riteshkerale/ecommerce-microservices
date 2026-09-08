
        package com.ritesh.notification_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmationEmail(
            Long userId,
            Long orderId,
            String email,
            String firstName
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("Order Confirmation - Order #" + orderId);

        message.setText(
                "Hello " + firstName + ",\n\n" +
                        "Your order has been successfully placed.\n\n" +
                        "Order ID: " + orderId + "\n" +
                        "User ID: " + userId + "\n\n" +
                        "Thank you for shopping with us!"
        );

        mailSender.send(message);

        System.out.println(
                "EMAIL SENT: Order " + orderId +
                        " confirmation email sent successfully"
        );
    }
}


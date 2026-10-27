
package pt.clutchmetrics.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String token) {

        String verificationUrl =
                baseUrl + "/api/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Verify your ClutchMetrics account");

        message.setText(
                "Welcome to ClutchMetrics!\n\n" +
                        "Thank you for joining our gaming community.\n\n" +
                        "To activate your account, please open the following link:\n\n" +
                        verificationUrl +
                        "\n\nThis link is valid for 24 hours.\n\n" +
                        "If you did not create this account, you can ignore this email."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String token) {

        String resetUrl =
                "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Reset your ClutchMetrics password");

        message.setText(
                "We received a request to reset your ClutchMetrics account password.\n\n" +
                        "To set a new password, please open the following link:\n\n" +
                        resetUrl +
                        "\n\nThis link is valid for 30 minutes.\n\n" +
                        "If you did not request a password reset, you can safely ignore this email."
        );

        mailSender.send(message);
    }

    public void sendPasswordChangeConfirmationEmail(
            String email,
            String token
    ) {


        String confirmationUrl =
                "http://localhost:3000/confirm-password-change?token=" + token;


        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);

        message.setSubject(
                "Confirm your ClutchMetrics password change"
        );

        message.setText(
                "Hello!\n\n" +
                        "We received a request to change your ClutchMetrics account password.\n\n" +
                        "To confirm this change, open the following link:\n\n" +
                        confirmationUrl +
                        "\n\nThis link is valid for 15 minutes.\n\n" +
                        "Your current password will remain active until you confirm the change.\n\n" +
                        "If you did not request this change, ignore this email and consider securing your account.\n\n" +
                        "ClutchMetrics Security"
        );

        mailSender.send(message);
    }

}


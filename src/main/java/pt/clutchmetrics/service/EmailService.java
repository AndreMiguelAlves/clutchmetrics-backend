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
        message.setSubject("Confirma a tua conta ClutchMetrics");

        message.setText(
                "Bem-vindo ao ClutchMetrics!\n\n" +
                        "Para confirmares a tua conta, abre o seguinte link:\n\n" +
                        verificationUrl +
                        "\n\nEste link é válido durante 24 horas."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String token) {

        String resetUrl =
                "http://localhost:3000/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Recuperação de password - ClutchMetrics");

        message.setText(
                "Recebemos um pedido para alterar a password da tua conta.\n\n" +
                        "Utiliza o seguinte link:\n\n" +
                        resetUrl +
                        "\n\nEste link é válido durante 30 minutos.\n\n" +
                        "Se não fizeste este pedido, ignora este email."
        );

        mailSender.send(message);
    }
}

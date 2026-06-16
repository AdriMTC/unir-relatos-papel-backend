package com.unir.comms.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendOrderConfirmation(String to, Long orderId, String bookTitle,
                                      Integer quantity, BigDecimal total) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Pedido #" + orderId + " confirmado — Relatos de Papel");
            message.setText(
                "¡Gracias por tu compra!\n\n" +
                "Tu pedido #" + orderId + " ha sido recibido con éxito.\n\n" +
                "  Libro:    " + bookTitle + "\n" +
                "  Cantidad: " + quantity + "\n" +
                "  Total:    " + total + " €\n\n" +
                "Te avisaremos cuando sea enviado.\n\n" +
                "— Relatos de Papel"
            );
            mailSender.send(message);
            log.info("Email de confirmación enviado a {} para pedido #{}", to, orderId);
        } catch (Exception e) {
            log.error("Error enviando email a {} para pedido #{}: {}", to, orderId, e.getMessage());
        }
    }
}

package org.example.saadtechstore.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Domain.Order;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(Order order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getCustomerEmail());
            message.setSubject("ModaStore — Commande confirmée #" + order.getId());
            message.setText(buildEmailBody(order));
            mailSender.send(message);
            log.info("Email envoyé à {}", order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Erreur envoi email : {}", e.getMessage());
        }
    }

    private String buildEmailBody(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bonjour ").append(order.getCustomerName()).append(",\n\n");
        sb.append("Votre commande a bien été confirmée !\n\n");
        sb.append("Récapitulatif :\n");
        order.getItems().forEach(item ->
                sb.append("- ").append(item.getProduct().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" = ").append(item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .append(" MAD\n"));
        sb.append("\nTotal : ").append(order.getTotalAmount()).append(" MAD\n");
        sb.append("Livraison à : ").append(order.getShippingAddress())
                .append(", ").append(order.getCity()).append("\n\n");
        sb.append("Merci pour votre achat !\nL'équipe ModaStore");
        return sb.toString();
    }
}
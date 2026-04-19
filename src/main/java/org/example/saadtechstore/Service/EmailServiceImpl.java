package org.example.saadtechstore.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject(
                    "✅ Commande confirmée #" +
                            order.getId().substring(0, 8).toUpperCase());
            helper.setText(buildConfirmationHtml(order), true);

            mailSender.send(message);
            log.info("Email confirmation envoyé à {}",
                    order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Erreur envoi email confirmation : {}",
                    e.getMessage());
        }
    }

    @Override
    public void sendOrderStatusUpdate(Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getCustomerEmail());
            helper.setSubject(
                    "📦 Mise à jour commande #" +
                            order.getId().substring(0, 8).toUpperCase());
            helper.setText(buildStatusUpdateHtml(order), true);

            mailSender.send(message);
            log.info("Email statut envoyé à {}",
                    order.getCustomerEmail());
        } catch (Exception e) {
            log.error("Erreur envoi email statut : {}",
                    e.getMessage());
        }
    }

    private String buildConfirmationHtml(Order order) {
        StringBuilder items = new StringBuilder();
        order.getItems().forEach(item ->
                items.append(String.format("""
                <tr>
                  <td style="padding:8px;border-bottom:1px solid #f3f4f6">
                    %s
                  </td>
                  <td style="padding:8px;border-bottom:1px solid #f3f4f6;
                             text-align:center">
                    %d
                  </td>
                  <td style="padding:8px;border-bottom:1px solid #f3f4f6;
                             text-align:right;color:#db2777;font-weight:bold">
                    %s MAD
                  </td>
                </tr>
                """,
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                                .toString()
                ))
        );

        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family:Arial,sans-serif;
                         background:#f9fafb;margin:0;padding:20px">
              <div style="max-width:600px;margin:0 auto;
                          background:white;border-radius:12px;
                          overflow:hidden;box-shadow:0 1px 3px rgba(0,0,0,.1)">
                
                <!-- Header -->
                <div style="background:#db2777;padding:24px;text-align:center">
                  <h1 style="color:white;margin:0;font-size:24px">
                    ✅ Commande Confirmée !
                  </h1>
                  <p style="color:#fce7f3;margin:8px 0 0">SaadStore</p>
                </div>

                <!-- Body -->
                <div style="padding:24px">
                  <p style="color:#374151;font-size:16px">
                    Bonjour <strong>%s</strong>,
                  </p>
                  <p style="color:#6b7280">
                    Votre commande a bien été reçue et est en cours
                    de traitement.
                  </p>

                  <!-- Order ID -->
                  <div style="background:#fdf2f8;border-radius:8px;
                              padding:12px;margin:16px 0;text-align:center">
                    <span style="color:#9ca3af;font-size:12px">
                      Numéro de commande
                    </span>
                    <p style="color:#db2777;font-weight:bold;
                               font-size:18px;margin:4px 0">
                      #%s
                    </p>
                  </div>

                  <!-- Items -->
                  <table style="width:100%%;border-collapse:collapse;
                                margin:16px 0">
                    <thead>
                      <tr style="background:#f9fafb">
                        <th style="padding:8px;text-align:left;
                                   color:#6b7280;font-size:12px">
                          Produit
                        </th>
                        <th style="padding:8px;text-align:center;
                                   color:#6b7280;font-size:12px">
                          Qté
                        </th>
                        <th style="padding:8px;text-align:right;
                                   color:#6b7280;font-size:12px">
                          Prix
                        </th>
                      </tr>
                    </thead>
                    <tbody>%s</tbody>
                  </table>

                  <!-- Total -->
                  <div style="border-top:2px solid #f3f4f6;
                              padding-top:12px;text-align:right">
                    <span style="font-size:18px;font-weight:bold;
                                 color:#db2777">
                      Total : %s MAD
                    </span>
                  </div>

                  <!-- Delivery info -->
                  <div style="background:#f9fafb;border-radius:8px;
                              padding:16px;margin:16px 0">
                    <h3 style="color:#374151;margin:0 0 8px;font-size:14px">
                      📍 Livraison
                    </h3>
                    <p style="color:#6b7280;margin:0;font-size:14px">
                      %s, %s<br>
                      📞 %s
                    </p>
                  </div>
                </div>

                <!-- Footer -->
                <div style="background:#f9fafb;padding:16px;
                            text-align:center;border-top:1px solid #f3f4f6">
                  <p style="color:#9ca3af;font-size:12px;margin:0">
                    Merci pour votre achat chez SaadStore 🛍️
                  </p>
                </div>
              </div>
            </body>
            </html>
            """,
                order.getCustomerName(),
                order.getId().substring(0, 8).toUpperCase(),
                items.toString(),
                order.getTotalAmount().toString(),
                order.getShippingAddress(),
                order.getCity(),
                order.getPhone()
        );
    }

    private String buildStatusUpdateHtml(Order order) {
        String statusLabel = switch (order.getStatus()) {
            case PAID -> "✅ Payée";
            case PROCESSING -> "⚙️ En cours de traitement";
            case SHIPPED -> "🚚 Expédiée";
            case DELIVERED -> "📦 Livrée";
            case CANCELLED -> "❌ Annulée";
            default -> order.getStatus().name();
        };

        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family:Arial,sans-serif;
                         background:#f9fafb;margin:0;padding:20px">
              <div style="max-width:600px;margin:0 auto;
                          background:white;border-radius:12px;
                          overflow:hidden;box-shadow:0 1px 3px rgba(0,0,0,.1)">
                <div style="background:#db2777;padding:24px;text-align:center">
                  <h1 style="color:white;margin:0;font-size:24px">
                    Mise à jour de commande
                  </h1>
                </div>
                <div style="padding:24px">
                  <p style="color:#374151">
                    Bonjour <strong>%s</strong>,
                  </p>
                  <p style="color:#6b7280">
                    Le statut de votre commande
                    <strong>#%s</strong> a été mis à jour :
                  </p>
                  <div style="background:#fdf2f8;border-radius:8px;
                              padding:16px;text-align:center;margin:16px 0">
                    <span style="font-size:20px;font-weight:bold;
                                 color:#db2777">
                      %s
                    </span>
                  </div>
                </div>
                <div style="background:#f9fafb;padding:16px;
                            text-align:center">
                  <p style="color:#9ca3af;font-size:12px;margin:0">
                    SaadStore — Merci pour votre confiance 🛍️
                  </p>
                </div>
              </div>
            </body>
            </html>
            """,
                order.getCustomerName(),
                order.getId().substring(0, 8).toUpperCase(),
                statusLabel
        );
    }
}
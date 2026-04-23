package org.example.saadtechstore.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Domain.Order;
import org.example.saadtechstore.Domain.Product;
import org.example.saadtechstore.Dto.ChatRequest;
import org.example.saadtechstore.Dto.ChatResponse;
import org.example.saadtechstore.Repository.OrderRepository;
import org.example.saadtechstore.Repository.ProductRepository;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ChatResponse chat(ChatRequest request) {

        String msg = request.getMessage().toLowerCase();

        // =========================
        // 📦 DETECTION COMMANDE
        // =========================
        if (msg.matches(".*#?[A-Z0-9]{6,}.*")) {

            String orderNumber = msg.replaceAll("[^A-Z0-9]", "");

            Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);

            if (order.isPresent()) {
                Order o = order.get();

                return response("📦 Commande " + orderNumber +
                        "\nStatut : " + o.getStatus() +
                        "\nTotal : " + o.getTotalAmount() + " MAD");
            } else {
                return response("❌ Commande introuvable. Vérifiez le numéro.");
            }
        } // ✅ IMPORTANT: fermeture du bloc commande

        // =========================
        // 🚚 FAQ
        // =========================
        if (msg.contains("livraison")) {
            return response("🚚 Livraison en 24-48h au Maroc. Gratuite dès 500 MAD.");
        }

        if (msg.contains("paiement")) {
            return response("💰 Paiement à la livraison (cash).");
        }

        if (msg.contains("retour")) {
            return response("🔄 Retour possible sous 7 jours.");
        }

        // =========================
        // 👟 MARQUES
        // =========================
        if (msg.contains("nike") || msg.contains("adidas") || msg.contains("puma")) {

            List<Product> products = productRepository.findAll()
                    .stream()
                    .filter(p -> p.getBrand() != null &&
                            msg.contains(p.getBrand().toLowerCase()))
                    .limit(3)
                    .toList();

            if (products.isEmpty()) {
                return response("❌ Aucun produit trouvé pour cette marque.");
            }

            StringBuilder res = new StringBuilder("👟 Voici quelques produits :\n\n");

            for (Product p : products) {
                res.append("- ")
                        .append(p.getName())
                        .append(" : ")
                        .append(p.getPrice())
                        .append(" MAD\n");
            }

            return response(res.toString());
        }

        // =========================
        // 💰 PRIX
        // =========================
        if (msg.contains("moins") || msg.contains("<")) {

            List<Product> products = productRepository.findAll()
                    .stream()
                    .filter(p -> p.getPrice().doubleValue() < 500)
                    .limit(3)
                    .toList();

            StringBuilder res = new StringBuilder("💰 Produits moins de 500 MAD :\n\n");

            for (Product p : products) {
                res.append("- ")
                        .append(p.getName())
                        .append(" : ")
                        .append(p.getPrice())
                        .append(" MAD\n");
            }

            return response(res.toString());
        }

        // =========================
        // 📦 COMMANDES INFO
        // =========================
        if (msg.contains("commande")) {
            return response("📦 Donnez votre numéro de commande pour suivi.");
        }

        // =========================
        // 🤖 DEFAULT
        // =========================
        return response("🤖 Je peux vous aider avec produits, livraison ou commandes.");
    }

    // =========================
    // 🔥 RESPONSE HELPER
    // =========================
    private ChatResponse response(String msg) {
        return ChatResponse.builder()
                .message(msg)
                .role("assistant")
                .build();
    }
}
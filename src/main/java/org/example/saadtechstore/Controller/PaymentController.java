package org.example.saadtechstore.Controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Domain.Product;
import org.example.saadtechstore.Dto.PaymentRequest;
import org.example.saadtechstore.Dto.PaymentResponse;
import org.example.saadtechstore.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final ProductRepository productRepository;

    @PostMapping("/checkout")
    public PaymentResponse createCheckoutSession(
            @RequestBody PaymentRequest req) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        // Si items fournis → calculer depuis les produits
        if (req.getItems() != null && !req.getItems().isEmpty()) {
            for (PaymentRequest.ItemDto item : req.getItems()) {
                Product product = productRepository
                        .findById(item.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException("Produit introuvable: "
                                        + item.getProductId()));

                lineItems.add(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(item.getQuantity().longValue())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(
                                                        product.getPrice()
                                                                .multiply(new java.math.BigDecimal(100))
                                                                .longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(product.getName())
                                                                .build())
                                                .build())
                                .build());
            }
        } else if (req.getAmount() != null) {
            // Fallback → montant fixe
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("mad")
                                            .setUnitAmount(
                                                    req.getAmount().longValue() * 100)
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData
                                                            .ProductData.builder()
                                                            .setName("Commande SaadStore")
                                                            .build())
                                            .build())
                            .build());
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/success")
                .setCancelUrl("http://localhost:4200/cart")
                .addAllLineItem(lineItems)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();

        Session session = Session.create(params);

        return PaymentResponse.builder()
                .cmiPaymentUrl(session.getUrl())
                .sessionId(session.getId())
                .status("CREATED")
                .build();
    }
}
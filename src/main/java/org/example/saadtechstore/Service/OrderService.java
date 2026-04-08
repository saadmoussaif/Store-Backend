package org.example.saadtechstore.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Domain.Enum.OrderStatut;
import org.example.saadtechstore.Domain.Order;
import org.example.saadtechstore.Domain.OrderItem;
import org.example.saadtechstore.Domain.Product;
import org.example.saadtechstore.Dto.OrderRequest;
import org.example.saadtechstore.Dto.OrderResponse;
import org.example.saadtechstore.Exception.ResourceNotFoundException;
import org.example.saadtechstore.Repository.OrderRepository;
import org.example.saadtechstore.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public OrderResponse createOrder(OrderRequest req, Jwt jwt) {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.ItemDto itemDto : req.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));

            if (product.getStock() < itemDto.getQuantity()) {
                throw new IllegalStateException("Stock insuffisant pour " + product.getName());
            }

            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            items.add(item);
            total = total.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity())));
        }

        Order order = Order.builder()
                .customerId(jwt.getSubject())
                .customerEmail(jwt.getClaimAsString("email"))
                .customerName(jwt.getClaimAsString("name"))
                .items(items)
                .totalAmount(total)
                .status(OrderStatut.PENDING)
                .shippingAddress(req.getShippingAddress())
                .city(req.getCity())
                .phone(req.getPhone())
                .build();

        items.forEach(i -> i.setOrder(order));
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public void confirmPayment(String orderId, String cmiTransactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Commande introuvable"));
        order.setStatus(OrderStatut.PAID);
        order.setCmiTransactionId(cmiTransactionId);
        orderRepository.save(order);
        emailService.sendOrderConfirmation(order);
    }

    public Page<OrderResponse> getMyOrders(String customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(this::toResponse);
    }

    public Page<OrderResponse> getAllOrders(OrderStatut status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable).map(this::toResponse);
        }
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .customerEmail(o.getCustomerEmail())
                .customerName(o.getCustomerName())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .shippingAddress(o.getShippingAddress())
                .city(o.getCity())
                .phone(o.getPhone())
                .createdAt(o.getCreatedAt())
                .items(o.getItems().stream().map(i -> OrderResponse.ItemDto.builder()
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList())
                .build();
    }
    public void updateStatus(String id, OrderStatut status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        order.setStatus(status);
        orderRepository.save(order);
    }
}


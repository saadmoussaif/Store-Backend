package org.example.saadtechstore.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.saadtechstore.Domain.Enum.OrderStatut;
import org.example.saadtechstore.Domain.Order;
import org.example.saadtechstore.Domain.OrderItem;
import org.example.saadtechstore.Domain.Product;
import org.example.saadtechstore.Dto.OrderRequest;
import org.example.saadtechstore.Dto.OrderResponse;
import org.example.saadtechstore.Exception.InsufficientStockException;
import org.example.saadtechstore.Exception.ResourceNotFoundException;
import org.example.saadtechstore.Repository.OrderRepository;
import org.example.saadtechstore.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponse createOrder(OrderRequest req) {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.ItemDto itemDto : req.getItems()) {
            Product product = productRepository
                    .findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produit introuvable : " + itemDto.getProductId()));

            if (product.getStock() < itemDto.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        itemDto.getQuantity(),
                        product.getStock());
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
                .customerId("anonymous")
                .customerEmail(req.getPhone() + "@saadstore.ma")
                .customerName("Client")
                .items(items)
                .totalAmount(total)
                .status(OrderStatut.PENDING)
                .shippingAddress(req.getShippingAddress())
                .city(req.getCity())
                .phone(req.getPhone())
                .build();

        items.forEach(i -> i.setOrder(order));
        Order saved = orderRepository.save(order);
        log.info("Commande créée : {}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public Page<OrderResponse> getMyOrders(
            String customerId, Pageable pageable) {
        return orderRepository
                .findByCustomerId(customerId, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrders(
            OrderStatut status, Pageable pageable) {
        if (status != null) {
            return orderRepository
                    .findByStatus(status, pageable)
                    .map(this::toResponse);
        }
        return orderRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public void updateStatus(String id, OrderStatut status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande introuvable : " + id));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("Statut commande {} mis à jour : {}", id, status);
    }

    @Override
    public void confirmPayment(String orderId, String cmiTransactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande introuvable : " + orderId));
        order.setStatus(OrderStatut.PAID);
        order.setCmiTransactionId(cmiTransactionId);
        orderRepository.save(order);
        log.info("Paiement confirmé pour commande : {}", orderId);
    }

    private OrderResponse toResponse(Order o) {
        List<OrderResponse.ItemDto> itemDtos = o.getItems().stream()
                .map(i -> OrderResponse.ItemDto.builder()
                        .productName(i.getProduct().getName())
                        .productImageUrl(i.getProduct().getImageUrl())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .subtotal(i.getUnitPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(o.getId())
                .customerEmail(o.getCustomerEmail())
                .customerName(o.getCustomerName())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .shippingAddress(o.getShippingAddress())
                .city(o.getCity())
                .phone(o.getPhone())
                .items(itemDtos)
                .createdAt(o.getCreatedAt())
                .build();
    }
}
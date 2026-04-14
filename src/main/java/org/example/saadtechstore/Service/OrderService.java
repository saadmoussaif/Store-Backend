package org.example.saadtechstore.Service;

import org.example.saadtechstore.Domain.Enum.OrderStatut;
import org.example.saadtechstore.Dto.OrderRequest;
import org.example.saadtechstore.Dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(OrderRequest req);

    Page<OrderResponse> getMyOrders(String customerId, Pageable pageable);

    Page<OrderResponse> getAllOrders(OrderStatut status, Pageable pageable);

    void updateStatus(String id, OrderStatut status);

    void confirmPayment(String orderId, String cmiTransactionId);
}
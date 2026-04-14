package org.example.saadtechstore.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Dto.OrderRequest;
import org.example.saadtechstore.Dto.OrderResponse;
import org.example.saadtechstore.Service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest req) {
        return orderService.createOrder(req);
    }

    @GetMapping("/my")
    public Page<OrderResponse> myOrders(Pageable pageable) {
        return orderService.getMyOrders("anonymous", pageable);
    }
}
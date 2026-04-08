package org.example.saadtechstore.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Dto.OrderRequest;
import org.example.saadtechstore.Dto.OrderResponse;
import org.example.saadtechstore.Service.OrderService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        return orderService.createOrder(req, jwt);
    }

    @GetMapping("/my")
    public Page<OrderResponse> myOrders(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        return orderService.getMyOrders(jwt.getSubject(), pageable);
    }
}

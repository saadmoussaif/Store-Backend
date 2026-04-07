package org.example.saadtechstore.Controller;

import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Service.OrderService;
import org.example.saadtechstore.Service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
}

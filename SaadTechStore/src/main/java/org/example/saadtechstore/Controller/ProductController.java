package org.example.saadtechstore.Controller;

import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
}

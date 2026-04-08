package org.example.saadtechstore.Controller;

import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Domain.Enum.Category;
import org.example.saadtechstore.Dto.ProductResponse;
import org.example.saadtechstore.Service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return productService.getAllProducts(category, search, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable String id) {
        return productService.getById(id);
    }
}

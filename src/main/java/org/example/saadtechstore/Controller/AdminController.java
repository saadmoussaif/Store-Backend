package org.example.saadtechstore.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Domain.Enum.OrderStatut;
import org.example.saadtechstore.Dto.OrderResponse;
import org.example.saadtechstore.Dto.ProductRequest;
import org.example.saadtechstore.Dto.ProductResponse;
import org.example.saadtechstore.Service.OrderService;
import org.example.saadtechstore.Service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;

    // Accepte JSON pur (sans image)
    @PostMapping(
            value = "/products",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProductJson(
            @RequestBody @Valid ProductRequest req) {
        return productService.create(req, null);
    }

    // Accepte multipart (avec image optionnelle)
    @PostMapping(
            value = "/products",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProductMultipart(
            @RequestPart("product") @Valid ProductRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.create(req, image);
    }

    // Accepte JSON pur (sans image)
    @PutMapping(
            value = "/products/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ProductResponse updateProductJson(
            @PathVariable String id,
            @RequestBody @Valid ProductRequest req) {
        return productService.update(id, req, null);
    }

    // Accepte multipart (avec image optionnelle)
    @PutMapping(
            value = "/products/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProductResponse updateProductMultipart(
            @PathVariable String id,
            @RequestPart("product") @Valid ProductRequest req,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return productService.update(id, req, image);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String id) {
        productService.delete(id);
    }

    @GetMapping("/orders")
    public Page<OrderResponse> getAllOrders(
            @RequestParam(required = false) OrderStatut status,
            Pageable pageable) {
        return orderService.getAllOrders(status, pageable);
    }

    @PutMapping("/orders/{id}/status")
    public void updateStatus(
            @PathVariable String id,
            @RequestParam OrderStatut status) {
        orderService.updateStatus(id, status);
    }
}
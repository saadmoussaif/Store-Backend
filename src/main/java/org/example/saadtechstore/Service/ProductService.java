package org.example.saadtechstore.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.saadtechstore.Domain.Enum.Category;
import org.example.saadtechstore.Domain.Product;
import org.example.saadtechstore.Dto.ProductRequest;
import org.example.saadtechstore.Dto.ProductResponse;
import org.example.saadtechstore.Exception.ResourceNotFoundException;
import org.example.saadtechstore.Repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final StorageService storageService;

    public Page<ProductResponse> getAllProducts(
            Category category, String search, Pageable pageable) {
        if (category != null && search != null) {
            return productRepository
                    .findByCategoryAndNameContainingIgnoreCaseAndActiveTrue(
                            category, search, pageable)
                    .map(this::toResponse);
        }
        if (category != null) {
            return productRepository
                    .findByCategoryAndActiveTrue(category, pageable)
                    .map(this::toResponse);
        }
        return productRepository
                .findByActiveTrue(pageable)
                .map(this::toResponse);
    }

    public ProductResponse getById(String id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
    }

    public ProductResponse create(ProductRequest req, MultipartFile image) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = storageService.uploadImage(image);
        }
        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock())
                .category(req.getCategory())
                .size(req.getSize())
                .color(req.getColor())
                .imageUrl(imageUrl)
                .active(true)
                .build();
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(String id, ProductRequest req, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setCategory(req.getCategory());
        product.setSize(req.getSize());
        product.setColor(req.getColor());
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(storageService.uploadImage(image));
        }
        return toResponse(productRepository.save(product));
    }

    public void delete(String id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable"));
        p.setActive(false);
        productRepository.save(p);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId()).name(p.getName())
                .description(p.getDescription()).price(p.getPrice())
                .stock(p.getStock()).imageUrl(p.getImageUrl())
                .category(p.getCategory()).size(p.getSize())
                .color(p.getColor()).active(p.isActive())
                .createdAt(p.getCreatedAt()).build();
    }
}

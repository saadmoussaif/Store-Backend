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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final StorageService storageService;

    // ← MÉTHODE MODIFIÉE — ajout brand
    public Page<ProductResponse> getAllProducts(
            Category category, String search,
            String brand, Pageable pageable) {

        // Recherche par nom (priorité absolue)
        if (search != null && !search.isBlank()) {
            if (category != null) {
                return productRepository
                        .findByCategoryAndNameContainingIgnoreCaseAndActiveTrue(
                                category, search, pageable)
                        .map(this::toResponse);
            }
            return productRepository
                    .findByNameContainingIgnoreCaseAndActiveTrue(
                            search, pageable)
                    .map(this::toResponse);
        }

        // Filtre brand
        if (brand != null && !brand.isBlank()) {
            if (category != null) {
                return productRepository
                        .findByCategoryAndBrandContainingIgnoreCaseAndActiveTrue(
                                category, brand, pageable)
                        .map(this::toResponse);
            }
            return productRepository
                    .findByBrandContainingIgnoreCaseAndActiveTrue(
                            brand, pageable)
                    .map(this::toResponse);
        }

        // Filtre catégorie seule
        if (category != null) {
            return productRepository
                    .findByCategoryAndActiveTrue(category, pageable)
                    .map(this::toResponse);
        }

        // Tous les produits actifs
        return productRepository
                .findByActiveTrue(pageable)
                .map(this::toResponse);
    }

    // ← MÉTHODES INCHANGÉES
    public ProductResponse getById(String id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable"));
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
                .brand(req.getBrand())
                .imageUrl(imageUrl)
                .active(true)
                .build();
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(
            String id, ProductRequest req, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable"));
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        product.setCategory(req.getCategory());
        product.setSize(req.getSize());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());
        if (image != null && !image.isEmpty()) {
            product.setImageUrl(storageService.uploadImage(image));
        }
        return toResponse(productRepository.save(product));
    }

    public void delete(String id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable"));
        p.setActive(false);
        productRepository.save(p);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .category(p.getCategory())
                .size(p.getSize())
                .color(p.getColor())
                .brand(p.getBrand())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
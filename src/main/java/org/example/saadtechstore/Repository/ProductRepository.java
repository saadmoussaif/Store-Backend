package org.example.saadtechstore.Repository;

import org.example.saadtechstore.Domain.Enum.Category;
import org.example.saadtechstore.Domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryAndActiveTrue(
            Category category, Pageable pageable);

    Page<Product> findByCategoryAndNameContainingIgnoreCaseAndActiveTrue(
            Category category, String name, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true AND p.category = :category")
    long countByCategory(@Param("category") Category category);
}

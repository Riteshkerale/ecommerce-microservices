package com.ritesh.product_service.repository;

import com.ritesh.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ==========================
    // Basic CRUD Helper Methods
    // ==========================

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Product> findByActiveTrue();

    List<Product> findByBrandIgnoreCase(String brand);

    List<Product> findByNameContainingIgnoreCase(String name);

    // ==========================
    // Search Products
    // ==========================

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.active = true
              AND p.stockQuantity > 0
              AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY p.createdAt DESC
            """)
    List<Product> searchAvailableProducts(@Param("keyword") String keyword);

    // ==========================
    // Price Filter
    // ==========================

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // ==========================
    // Stock Management
    // ==========================

    List<Product> findByStockQuantityGreaterThan(Integer quantity);

}
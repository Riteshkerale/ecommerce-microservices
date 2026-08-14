package com.ritesh.product_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "sku")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Product Name
    @Column(nullable = false, length = 200)
    private String name;

    // Product Description
    @Column(nullable = false, length = 3000)
    private String description;

    // Selling Price
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Available Stock
    @Column(nullable = false)
    private Integer stockQuantity;

    // Unique Product Code
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    // Product Brand
    @Column(length = 100)
    private String brand;

    // Thumbnail Image
    private String imageUrl;

    // Product Available?
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
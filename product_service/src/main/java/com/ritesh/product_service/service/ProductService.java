package com.ritesh.product_service.service;

import com.ritesh.product_service.config.ProductCacheProperties;
import com.ritesh.product_service.dtos.request.ProductRequest;
import com.ritesh.product_service.dtos.response.ProductResponse;
import com.ritesh.product_service.entity.Product;
import com.ritesh.product_service.exception.ProductNotFoundException;
import com.ritesh.product_service.repository.ProductRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCacheProperties productCacheProperties;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, ProductResponse> redisTemplate;

    // CREATE PRODUCT

    public ProductResponse createProduct(ProductRequest request) {
        if(productRepository.existsBySku(request.getSku())) {
            throw new RuntimeException(
                    "Product SKU already exists"
            );
        }
        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setBrand(request.getBrand());
        Product savedProduct =
                productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    // GET ALL PRODUCTS

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    // GET PRODUCT BY ID

    public ProductResponse getProductById(Long id) {

        String key = "product:" + id;

        // 1. Check Redis
        try {
            ProductResponse cachedProduct =
                    redisTemplate.opsForValue().get(key);

            if (cachedProduct != null) {
                System.out.println("CACHE HIT: " + key);
                return cachedProduct;
            }

        } catch (Exception e) {
            System.out.println(
                    "REDIS GET FAILED: " + e.getMessage()
            );
        }

        // 2. Cache miss → get from MySQL
        System.out.println("CACHE MISS: " + key);

        Product product =
                productRepository.findById(id)
                        .orElseThrow(
                                () -> new ProductNotFoundException(
                                        "Product not found with id : " + id
                                )
                        );

        ProductResponse response = mapToResponse(product);

        // 3. Store result in Redis for 10 minutes
        try {
            redisTemplate.opsForValue().set(
                    key,
                    response,
                    productCacheProperties.getProductTtl()
            );

        } catch (Exception e) {
            System.out.println(
                    "REDIS SET FAILED: " + e.getMessage()
            );
        }

        return response;
    }




    // UPDATE PRODUCT

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request
    ) {
        Product product =
                productRepository.findById(id)
                        .orElseThrow(
                                () -> new ProductNotFoundException(
                                        "Product not found with id : " + id
                                )
                        );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setBrand(request.getBrand());

        Product updatedProduct =
                productRepository.save(product);

        // Invalidate cache
        String key = "product:" + id;
        redisTemplate.delete(key);

        return mapToResponse(updatedProduct);
    }

// DELETE PRODUCT

    public void deleteProduct(Long id) {
        Product product =
                productRepository.findById(id)
                        .orElseThrow(
                                () -> new ProductNotFoundException(
                                        "Product not found with id : " + id
                                )
                        );
        productRepository.delete(product);

        // Invalidate cache
        String key = "product:" + id;
        redisTemplate.delete(key);

    }

    // SEARCH PRODUCTS

    public List<ProductResponse> searchProducts(String keyword) {

        return productRepository.searchAvailableProducts(keyword)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


//       Reduce stock

    public void reduceStock(Long productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found: " + productId
                        )
                );

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient stock for " + product.getName()
            );
        }

        product.setStockQuantity(
                product.getStockQuantity() - quantity
        );

        productRepository.save(product);


        // Invalidate cache
        String key = "product:" + productId;
        redisTemplate.delete(key);
    }

    // ENTITY TO DTO CONVERSION

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response =
                new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setSku(product.getSku());
        response.setBrand(product.getBrand());
        return response;
    }

}
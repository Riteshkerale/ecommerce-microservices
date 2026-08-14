package com.ritesh.product_service.service;

import com.ritesh.product_service.dtos.request.ProductRequest;
import com.ritesh.product_service.dtos.response.ProductResponse;
import com.ritesh.product_service.entity.Product;
import com.ritesh.product_service.exception.ProductNotFoundException;
import com.ritesh.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {


    private final ProductRepository productRepository;

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
        Product product =
                productRepository.findById(id)
                        .orElseThrow(
                                () -> new ProductNotFoundException(
                                        "Product not found with id : " + id
                                )
                        );
        return mapToResponse(product);
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
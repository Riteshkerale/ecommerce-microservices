package com.ritesh.product_service.controller;

import com.ritesh.product_service.dtos.request.ProductRequest;
import com.ritesh.product_service.dtos.response.ProductResponse;
import com.ritesh.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;


    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request
    ){

        ProductResponse response =
                productService.createProduct(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }



    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){

        List<ProductResponse> products =
                productService.getAllProducts();

        return ResponseEntity.ok(products);
    }




    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id
    ){

        ProductResponse product =
                productService.getProductById(id);

        return ResponseEntity.ok(product);
    }




    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ){

        ProductResponse updatedProduct =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(updatedProduct);
    }




    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id
    ){

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                productService.searchProducts(keyword)
        );
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> reduceStock(
            @PathVariable Long id,
            @RequestParam int quantity
    ) {
        productService.reduceStock(id, quantity);

        return ResponseEntity.ok().build();
    }

}
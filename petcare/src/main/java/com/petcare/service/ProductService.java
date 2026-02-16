package com.petcare.service;

import com.petcare.entity.Product;
import com.petcare.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Public API - List only active products
    public List<Product> listProducts(Long categoryId, String search) {
        if ((categoryId == null || categoryId == 0) && (search == null || search.isBlank())) {
            return productRepository.findByIsActive(true);
        }
        return productRepository.findByCategoryAndSearch(categoryId == null ? null : categoryId, search);
    }

    // Admin API - List all products (including inactive)
    public List<Product> listAllProducts(Long categoryId, String search) {
        if ((categoryId == null || categoryId == 0) && (search == null || search.isBlank())) {
            return productRepository.findAll();
        }
        return productRepository.findAllByCategoryAndSearch(categoryId == null ? null : categoryId, search);
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getPrice() != null) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getStockQuantity() != null) {
            product.setStockQuantity(productDetails.getStockQuantity());
        }
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getIsActive() != null) {
            product.setIsActive(productDetails.getIsActive());
        }
        
        return productRepository.save(product);
    }

    // Soft delete - mark as inactive
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }

    // Hard delete (if needed for admin)
    public void hardDeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}

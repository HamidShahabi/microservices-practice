package org.example.inventory.service;

import org.example.inventory.entity.Product;
import org.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final ProductRepository repository;

    public InventoryService(ProductRepository repository) {
        this.repository = repository;
    }

    public boolean reduceStock(String productId, int quantity) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            repository.save(product);
            return true;
        } else {
            return false;
        }
    }

    public void restoreStock(String productId, int quantity) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(product.getStock() + quantity);
        repository.save(product);
    }

    public void addProduct(Product product) {
        repository.save(product);
    }

    public Product getProduct(String productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}

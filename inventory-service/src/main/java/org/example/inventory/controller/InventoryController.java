package org.example.inventory.controller;

import org.example.inventory.entity.Product;
import org.example.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/reduce")
    public ResponseEntity<String> reduceStock(@RequestParam String productId, @RequestParam int quantity) {
        boolean success = service.reduceStock(productId, quantity);
        if (success) {
            return ResponseEntity.ok("Stock reduced successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient stock");
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreStock(@RequestParam String productId, @RequestParam int quantity) {
        service.restoreStock(productId, quantity);
        return ResponseEntity.ok("Stock restored successfully");
    }

    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        service.addProduct(product);
        return ResponseEntity.ok("Product added successfully");
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable String productId) {
        return service.getProduct(productId);
    }
}

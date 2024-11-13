package org.example.orchestrator.feign;

import org.example.orchestrator.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", url = "http://localhost:8083")
@Component
public interface InventoryServiceClient {

    @PostMapping("/inventory/reduce")
    ResponseEntity<String> reduceStock(@RequestParam String productId, @RequestParam int quantity);

    @PostMapping("/inventory/restore")
    ResponseEntity<String> restoreStock(@RequestParam String productId, @RequestParam int quantity);

    @GetMapping("/inventory/{productId}")
    Product getProduct(@PathVariable String productId);
}


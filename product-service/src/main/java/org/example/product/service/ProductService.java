package org.example.product.service;

import org.springframework.stereotype.Service;
import org.example.product.model.Product;
import org.example.product.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Flux<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Mono<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    public Mono<Product> createProduct(Product product){
        return productRepository.save(product);
    }

    public Mono<Product> updateProduct(Long id, Product product){
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantity(product.getQuantity());
                    return productRepository.save(existingProduct);
                });
    }

    public Mono<Void> deleteProduct(Long id){
        return productRepository.deleteById(id);
    }
}

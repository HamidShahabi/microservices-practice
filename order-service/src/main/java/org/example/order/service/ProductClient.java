package org.example.order.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {
    private final WebClient webClient;

    public ProductClient(@Value("${product-service.url}") String baseUrl){
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<ProductResponse> getProductById(Long id){
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }

    // Define ProductResponse class or use a shared library
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private Integer quantity;

        // Getters and Setters
        // ...
        public Long getId() {
            return id;
        }

        public void setId(Long id){
            this.id = id;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getDescription(){
            return description;
        }

        public void setDescription(String description){
            this.description = description;
        }

        public Double getPrice(){
            return price;
        }

        public void setPrice(Double price){
            this.price = price;
        }

        public Integer getQuantity(){
            return quantity;
        }

        public void setQuantity(Integer quantity){
            this.quantity = quantity;
        }
    }
}

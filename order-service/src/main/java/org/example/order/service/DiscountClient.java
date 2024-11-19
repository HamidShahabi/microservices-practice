package org.example.order.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DiscountClient {
    private final WebClient webClient;

    public DiscountClient(@Value("${discount-service.url}") String baseUrl){
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<DiscountResponse> getDiscountByCode(String code){
        return webClient.get()
                .uri("/code/{code}", code)
                .retrieve()
                .bodyToMono(DiscountResponse.class);
    }

    // Define DiscountResponse class or use a shared library
    public static class DiscountResponse {
        private Long id;
        private String code;
        private Double percentage;
        private String description;

        // Getters and Setters
        // ...
        public Long getId(){
            return id;
        }

        public void setId(Long id){
            this.id = id;
        }

        public String getCode(){
            return code;
        }

        public void setCode(String code){
            this.code = code;
        }

        public Double getPercentage(){
            return percentage;
        }

        public void setPercentage(Double percentage){
            this.percentage = percentage;
        }

        public String getDescription(){
            return description;
        }

        public void setDescription(String description){
            this.description = description;
        }
    }
}

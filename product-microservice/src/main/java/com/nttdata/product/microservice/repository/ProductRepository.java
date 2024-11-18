package com.nttdata.product.microservice.repository;

import com.nttdata.product.microservice.domain.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Mono<Product> findByClientId(String clientId);
}

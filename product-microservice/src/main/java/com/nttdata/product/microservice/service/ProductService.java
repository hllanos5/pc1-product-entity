package com.nttdata.product.microservice.service;

import com.nttdata.product.microservice.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> save(Mono<Product> product);
    Mono<Product> findById(String id);
    Flux<Product> findAll();
    Mono<Product> findByClientId(String clientId);
    Mono<Product> update(String id, Mono<Product> product);
    Mono<Void> delete(Product product);
}

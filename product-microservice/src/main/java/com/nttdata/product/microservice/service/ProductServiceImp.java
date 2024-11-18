package com.nttdata.product.microservice.service;

import com.nttdata.product.microservice.domain.Product;
import com.nttdata.product.microservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Mono<Product> save(Mono<Product> client) {
        return client.flatMap(productRepository::insert);
    }

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Mono<Product> findByClientId(String clientId) {
        return productRepository.findByClientId(clientId);
    }

    @Override
    public Mono<Product> update(String id, Mono<Product> product) {
        return productRepository.findById(id)
                .flatMap(c -> product)
                .doOnNext(e -> e.setId(id))
                .flatMap(productRepository::save);

    }

    @Override
    public Mono<Void> delete(Product product) {
        return productRepository.delete(product);
    }
}

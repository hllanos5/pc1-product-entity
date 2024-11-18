package com.nttdata.product.microservice.controller;

import com.mongodb.DuplicateKeyException;
import com.nttdata.product.microservice.api.ProductApi;
import com.nttdata.product.model.Product;
import com.nttdata.product.microservice.mapper.ProductMapper;
import com.nttdata.product.microservice.service.ProductService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class ProductController implements ProductApi {
    private static final String TIMESTAMP = "timestamp";
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;



    @Override
    public Mono<ResponseEntity<Map<String, Object>>> createProduct(Mono<Product> product, ServerWebExchange exchange) {
        Map<String, Object> response =  new HashMap<>();
        return productService.save(product.map(productMapper::toDocument))
                .map(productMapper::toModel)
                .map(c -> {
                    response.put("product",c);
                    response.put("message", "Successful product saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }).onErrorResume(getThrowableMonoFunction(response));


    }
    @Override
    public Mono<ResponseEntity<Void>> deleteProduct(String id, ServerWebExchange exchange) {
        return productService.findById(id)
                .flatMap(c -> productService.delete(c)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateProduct(String id, Mono<Product> client, ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        return productService.update(id, client.map(productMapper::toDocument))
                .map(productMapper::toModel)
                .map(c -> {
                    response.put("product", c);
                    response.put("message", "Successful product saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .onErrorResume(WebExchangeBindException.class, getThrowableMonoFunction(response))
                .onErrorResume(DuplicateKeyException.class, getThrowableDuplicate(response))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public  Mono<ResponseEntity<Flux<Map<String, Object>>>> findAllProduct(ServerWebExchange exchange) {
        Flux<com.nttdata.product.microservice.domain.Product> clientsFlux = productService.findAll();

        Flux<Map<String, Object>> productsMapFlux = clientsFlux.map(product -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("clientId", product.getClientId());
            productMap.put("type", product.getType());
            productMap.put("accountType", product.getAccountType());
            productMap.put("activeType", product.getActiveType());
            productMap.put("amount", product.getAmount());
            productMap.put("createAt", product.getCreatedAt());
            return productMap;
        });

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(productsMapFlux));

    }

    @Override
    public Mono<ResponseEntity<Product>> getProductByClientId(String clientId, ServerWebExchange exchange) {

        return productService.findByClientId(clientId)
                .map(productMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @Override
    public Mono<ResponseEntity<Product>> getProductById(String id, ServerWebExchange exchange) {
        return productService.findById(id)
                .map(productMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableMonoFunction(Map<String, Object> response){
        return t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "Campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableDuplicate(Map<String, Object> response){
        return t -> Mono.just(t).cast(DuplicateKeyException.class)
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }
}

package com.nttdata.product.microservice.controller;

import com.nttdata.product.microservice.api.HelloApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class HelloController implements HelloApi {

    public Mono<ResponseEntity<String>> helloGet(String nombre,  ServerWebExchange exchange) {
        System.out.println("=======> Llego"+ nombre);
        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(null));
    }
}

package com.nttdata.product.microservice.mapper;

public interface EntityMapper<D,E>{
    E toDocument(D model);
    D toModel(E domain);
}

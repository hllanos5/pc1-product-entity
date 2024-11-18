package com.nttdata.product.microservice.mapper;

import com.nttdata.product.model.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Date;

@Component
public class ProductMapper implements EntityMapper<Product, com.nttdata.product.microservice.domain.Product>{

    @Override
    public com.nttdata.product.microservice.domain.Product toDocument(Product model) {
        com.nttdata.product.microservice.domain.Product product= new com.nttdata.product.microservice.domain.Product();
        BeanUtils.copyProperties(model, product);
        product.setCreatedAt(new Date());
        return product;
    }

    @Override
    public Product toModel(com.nttdata.product.microservice.domain.Product domain) {
        Product product= new Product();
        BeanUtils.copyProperties(domain, product);
        product.setCreateAt(domain.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC));
        return product;
    }
}

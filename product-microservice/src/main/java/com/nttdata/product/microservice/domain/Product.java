package com.nttdata.product.microservice.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Getter
@Setter
@Document(collection  = "product")
public class Product {
    @Id
    private String id;
    private String clientId;
    private String type;
    private String accountType;
    private String activeType;
    @Indexed(unique = true)
    private String accountNumber;
    private Double amount;
    @CreatedDate
    private Date createdAt;

}

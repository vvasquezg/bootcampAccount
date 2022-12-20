package com.bootcamp.java.account.domain;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private String code;
    private ProductType productType;
    private Boolean active;
}

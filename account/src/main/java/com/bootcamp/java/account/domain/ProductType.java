package com.bootcamp.java.account.domain;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductType {
    private String id;
    private String name;
    private String code;
    private Boolean active;
}

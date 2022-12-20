package com.bootcamp.java.account.domain;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductParameter {
    private String id;
    private String productCode;
    private String clientType;
    private String clientProfile;
    private Long maxProduct;
    private Float commissionAccount;
    private Float commissionTransaction;
    private Long maxTransaction;
    private Integer transactionDay;
    private Integer minimumHolder;
    private Integer minimumSigner;
    private Boolean accountRequired;
    private Boolean cardRequired;
    private Float averageMinimumAmount;
    private Boolean active;
}

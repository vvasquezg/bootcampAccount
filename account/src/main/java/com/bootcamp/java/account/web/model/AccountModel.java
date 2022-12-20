package com.bootcamp.java.account.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {
    @JsonIgnore
    private String id;
    @NotBlank(message = "Product Code cannot be null or empty")
    private String productCode;
    @NotBlank(message = "Account Number cannot be null or empty")
    private String accountNumber;
    @NotBlank(message = "Card Number cannot be null or empty")
    private String cardNumber;
    @NotBlank(message = "Card Company cannot be null or empty")
    private String cardCompany;
    @NotBlank(message = "Bank Name cannot be null or empty")
    private String bankName;
    private Float amountAvailable;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate openingDate;
    @NotNull(message = "Client cannot be null or empty")
    private ClientDocumentModel client;
    private List<ClientDocumentModel> holder;
    private List<ClientDocumentModel> signer;
    private Boolean active;
}

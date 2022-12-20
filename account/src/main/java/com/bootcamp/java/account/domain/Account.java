package com.bootcamp.java.account.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = { "accountNumber" })
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "account")
public class Account {
    @Id
    private String id;
    @NotNull
    private String productCode;
    @NotNull
    @Indexed(unique = true)
    private String accountNumber;
    @NotNull
    private String cardNumber;
    @NotNull
    private String cardCompany;
    @NotNull
    private String bankName;
    @NotNull
    private Float amountAvailable;
    @NotNull
    private LocalDate openingDate;
    @NotNull
    private ClientDocument client;
    private List<ClientDocument> holder;
    private List<ClientDocument> signer;
    private Boolean active;
}

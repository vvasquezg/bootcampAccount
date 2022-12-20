package com.bootcamp.java.account.domain;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDocument {
    @NotNull
    private String identityDocumentType;
    @NotNull
    private String identityDocumentNumber;
}

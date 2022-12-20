package com.bootcamp.java.account.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocumentModel {
    @NotBlank(message = "Identity Document Type cannot be null or empty")
    private String identityDocumentType;
    @NotBlank(message = "Identity Document Number cannot be null or empty")
    private String identityDocumentNumber;
}

package com.bootcamp.java.account.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {
    @JsonIgnore
    private String id;
    @NotBlank(message = "Identity Document Type cannot be null or empty")
    private String identityDocumentType;
    @NotBlank(message = "Identity Document Number cannot be null or empty")
    private String identityDocumentNumber;
    private String name;
    private String lastName;
    private String businessName;
    private String email;
    private String phoneNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String clientType;
    private String clientProfile;
}

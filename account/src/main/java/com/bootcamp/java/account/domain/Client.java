package com.bootcamp.java.account.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = { "identityDocumentNumber" })
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    private String id;
    @NotNull
    private String identityDocumentType;
    @NotNull
    private String identityDocumentNumber;
    private String name;
    private String lastName;
    private String businessName;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private String clientType;
    private String clientProfile;
}

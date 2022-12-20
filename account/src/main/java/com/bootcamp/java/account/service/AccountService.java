package com.bootcamp.java.account.service;

import com.bootcamp.java.account.domain.Account;
import com.bootcamp.java.account.domain.ClientDocument;
import com.bootcamp.java.account.repository.AccountRepository;
import com.bootcamp.java.account.service.exception.InvalidClientException;
import com.bootcamp.java.account.service.exception.InvalidProductException;
import com.bootcamp.java.account.web.mapper.AccountMapper;
import com.bootcamp.java.account.web.model.ClientModel;
import com.bootcamp.java.account.web.model.ProductParameterModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;


    public Flux<Account> findAll(){
        log.debug("findAll executed");
        return accountRepository.findAll();
    }

    public Mono<Account> findById(String id){
        log.debug("findById executed {}", id);
        return accountRepository.findById(id);
    }

    public Mono<Account> findTopByAccountNumber(String accountNumber){
        log.debug("findTopByAccountNumber executed {}", accountNumber);
        return accountRepository.findTopByAccountNumber(accountNumber);
    }

    public Mono<Account> create(Account account) {
        log.debug("create executed {}", account);

        return validateClient(account.getClient()).flatMap(clientModel -> validateProductCode(account.getProductCode(), clientModel.getClientType(), clientModel.getClientProfile())
                .flatMap(productParameterModel -> {
                    Mono<Boolean> maxAccountValidated = validateMaxAccount(account.getClient(), account.getProductCode(), productParameterModel.getMaxProduct());
                    Mono<Boolean> holdersValidated = validateHolderRequired(account.getHolder(), productParameterModel.getMinimumHolder());
                    Mono<Boolean> signersValidated = validateSignerRequired(account.getSigner(), productParameterModel.getMinimumSigner());
                    Mono<Boolean> accountRequired = validateAccount(productParameterModel.getAccountRequired(), account.getClient());
                    Mono<Boolean> cardRequired = validateCard(productParameterModel.getCardRequired(), account.getClient());

                    return Mono.zip(maxAccountValidated, holdersValidated, signersValidated, accountRequired, cardRequired)
                            .flatMap(objects -> !objects.getT1() ? Mono.error(new Exception("Max. Account")) : !objects.getT2()
                                    ? Mono.error(new Exception(productParameterModel.getMinimumHolder().toString() + " Holder Required")) : !objects.getT3() ? Mono.error(new Exception(productParameterModel.getMinimumSigner().toString() + " Signer Required")) : !objects.getT4()
                                    ? Mono.error(new Exception("Account created is required")) : !objects.getT5() ? Mono.error(new Exception("Card created is required")) :
                                    accountRepository.save(account));
                }));
    }

    public Mono<Account> updateByAccountNumber(Account account) {
        log.debug("updateByAccountNumber executed {}", account);
        return accountRepository.findTopByAccountNumber(account.getAccountNumber())
                .flatMap(dbAccount -> {
                    accountMapper.update(dbAccount, account);
                    return accountRepository.save(dbAccount);
                });
    }

    public Mono<Long> countByClientAndProductCode(ClientDocument client, String productCode){
        log.debug("countByClientAndProductCode executed {} - {}", client, productCode);
        return accountRepository.countByClientAndProductCode(client, productCode);
    }

    public Mono<Long> countByClient(ClientDocument client){
        log.debug("countByClient executed {}", client);
        return accountRepository.countByClient(client);
    }

    private WebClient getWCClient(){
        log.debug("getWebClient executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9050/v1/client")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient getWCParameter(){
        log.debug("getWebClient executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9054/v1/productParameter")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private WebClient getWCCard(){
        log.debug("getWCCard executed");
        return WebClient.builder()
                .baseUrl("http://localhost:9056/v1/card")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<ClientModel> getClientByDocument(ClientDocument client){
        log.debug("getClientByDocument executed {}", client);
        return getWCClient().get().uri("/" + client.getIdentityDocumentNumber() + "/" + client.getIdentityDocumentType()).retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new InvalidClientException())).bodyToMono(ClientModel.class);
    }

    public Mono<ProductParameterModel> getParameterByCodeAndTypeAndProfile(String code, String type, String profile) {
        log.debug("getParameterByCodeAndTypeAndProfile executed {} - {} - {}", code, type, profile);
        return getWCParameter().get().uri("/getByCodeAndTypeAndProfile/" + code + "/" + type + "/" + profile).retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new InvalidProductException())).bodyToMono(ProductParameterModel.class);
    }

    public Mono<Long> countCardByClient(ClientDocument client){
        log.debug("countCardByClient executed {}", client);
        return getWCCard().get().uri("/countCardByClient/" + client.getIdentityDocumentType() + "/" + client.getIdentityDocumentNumber()).retrieve().bodyToMono(Long.class);
    }

    private Mono<ClientModel> validateClient(ClientDocument client){
        log.debug("validateClient executed {}", client);
        return getClientByDocument(client)
                .switchIfEmpty(Mono.error(new InvalidClientException()))
                .flatMap(Mono::just);
    }

    private Mono<ProductParameterModel> validateProductCode(String code, String type, String profile){
        return getParameterByCodeAndTypeAndProfile(code, type, profile)
                .switchIfEmpty(Mono.error(new Exception("Product, client type and client profile not configured")))
                .flatMap(Mono::just);
    }

    private Mono<Boolean> validateMaxAccount(ClientDocument client, String productCode, Long maxAccount){
        Mono<Long> countClientProduct = countByClientAndProductCode(client, productCode);

        return countClientProduct.flatMap(aLong -> aLong >= maxAccount ? Mono.just(false) : Mono.just(true));
    }

    private Mono<Boolean> validateHolderRequired(List<ClientDocument> holders, Integer minimumHolder){
        int countHolders = holders == null || holders.isEmpty() ? 0 : holders.size();
        return countHolders >= minimumHolder ? Mono.just(true) : Mono.just(false);
    }

    private Mono<Boolean> validateSignerRequired(List<ClientDocument> signers, Integer minimumSigner){
        int countSigners = signers == null || signers.isEmpty() ? 0 : signers.size();
        return countSigners >= minimumSigner ? Mono.just(true) : Mono.just(false);
    }

    private Mono<Boolean> validateAccount(Boolean accountRequired, ClientDocument client) {
        return accountRequired ?
                countByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }

    private Mono<Boolean> validateCard(Boolean cardRequired, ClientDocument client) {
        return cardRequired ?
                countCardByClient(client).flatMap(aLong -> aLong > 0 ? Mono.just(true) : Mono.just(false))
                : Mono.just(true);
    }
}

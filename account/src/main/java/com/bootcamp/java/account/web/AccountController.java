package com.bootcamp.java.account.web;

import com.bootcamp.java.account.domain.Account;
import com.bootcamp.java.account.domain.ClientDocument;
import com.bootcamp.java.account.service.AccountService;
import com.bootcamp.java.account.web.mapper.AccountMapper;
import com.bootcamp.java.account.web.model.AccountModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/account")
public class AccountController {
    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountMapper accountMapper;

    @GetMapping
    public Mono<ResponseEntity<Flux<AccountModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(accountService.findAll()
                        .map(client -> accountMapper.entityToModel(client))));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountModel>> getById(@PathVariable String id){
        log.info("getById executed {}", id);
        Mono<Account> response = accountService.findById(id);
        return response
                .map(customer -> accountMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/getByAccountNumber/{accountNumber}")
    public Mono<ResponseEntity<AccountModel>> getByAccountNumber(@PathVariable String accountNumber){
        log.info("getByAccountNumber executed {}", accountNumber);
        Mono<Account> response = accountService.findTopByAccountNumber(accountNumber);
        return response
                .map(customer -> accountMapper.entityToModel(customer))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<AccountModel>> create(@Valid @RequestBody AccountModel request){
        log.info("create executed {}", request);
        return accountService.create(accountMapper.modelToEntity(request))
                .map(client -> accountMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "account", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateByAccountNumber")
    public Mono<ResponseEntity<AccountModel>> updateByAccountNumber(@Valid @RequestBody AccountModel request){
        log.info("updateByAccountNumber executed {}", request);
        return accountService.updateByAccountNumber(accountMapper.modelToEntity(request))
                .map(client -> accountMapper.entityToModel(client))
                .flatMap(c ->
                        Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name,
                                        port, "account", c.getId())))
                                .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/countAccountByClient/{identityDocumentType}/{identityDocumentNumber}")
    public Mono<ResponseEntity<Long>> countAccountByClient(@PathVariable String identityDocumentType, @PathVariable String identityDocumentNumber){
        log.info("countAccountByClient executed {} - {}", identityDocumentType, identityDocumentNumber);

        Mono<Long> response = accountService.countByClient(new ClientDocument(identityDocumentType, identityDocumentNumber));
        return response
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

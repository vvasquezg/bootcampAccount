package com.bootcamp.java.account.repository;

import com.bootcamp.java.account.domain.Account;
import com.bootcamp.java.account.domain.ClientDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Long> countByClient(ClientDocument client);
    Mono<Long> countByClientAndProductCode(ClientDocument client, String productCode);
    Mono<Account> findTopByAccountNumber(String accountNumber);
}

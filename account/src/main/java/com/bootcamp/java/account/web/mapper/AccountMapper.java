package com.bootcamp.java.account.web.mapper;

import com.bootcamp.java.account.domain.Account;
import com.bootcamp.java.account.web.model.AccountModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account modelToEntity(AccountModel model);

    AccountModel entityToModel(Account event);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Account entity, Account updateEntity);
}

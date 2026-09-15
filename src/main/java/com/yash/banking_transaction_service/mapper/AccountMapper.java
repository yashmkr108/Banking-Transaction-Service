package com.yash.banking_transaction_service.mapper;

import com.yash.banking_transaction_service.dto.AccountResponse;
import com.yash.banking_transaction_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR

)
public interface AccountMapper {
    AccountResponse toResponse(Account account);
}

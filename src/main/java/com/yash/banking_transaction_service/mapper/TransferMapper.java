package com.yash.banking_transaction_service.mapper;

import com.yash.banking_transaction_service.dto.TransferResponse;
import com.yash.banking_transaction_service.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TransferMapper {
    @Mapping(
            target = "sourceAccountNumber",
            source = "sourceAccount.accountNumber"
    )
    @Mapping(
            target = "destinationAccountNumber",
            source = "destinationAccount.accountNumber"
    )
    TransferResponse toResponse(Transfer transfer);
}

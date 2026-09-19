package com.yash.banking_transaction_service.exceptions.transfer.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class TransferReferenceGenerator {

    public String generate(){
        String date = LocalDate.now()
                .toString()
                .replace("-","");

        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-","")
                .toUpperCase();

        return "TRF-" + date + "-" + randomPart;
    }
}

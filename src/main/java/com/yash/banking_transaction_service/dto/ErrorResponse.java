package com.yash.banking_transaction_service.dto;

import java.util.List;
import java.util.Map;

public record ErrorResponse(int status, String message, Map<String, List<String>> errors, String timestamp) {
}

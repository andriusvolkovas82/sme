package sme.account.model.request;

import sme.account.model.AccountOperation;

import java.math.BigDecimal;

public record TransferOperationRequest(
        Long accountNumber,
        BigDecimal amount,
        Integer currency,
        AccountOperation operationType
) {
}

package sme.account.service;

import sme.account.model.AccountStatus;
import sme.account.model.request.TransferOperationRequest;
import sme.account.model.response.AccountBalanceResponse;

public interface AccountService {

    AccountStatus getStatus(Long accountNumber);

    AccountBalanceResponse balanceInquiry(Long accountNumber);

    void transfer(TransferOperationRequest request);

}

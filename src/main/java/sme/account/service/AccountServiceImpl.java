package sme.account.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import sme.account.model.AccountStatus;
import sme.account.model.entity.Account;
import sme.account.model.request.TransferOperationRequest;
import sme.account.model.response.AccountBalanceResponse;
import sme.account.repository.AccountRepository;

import java.math.BigDecimal;

import static sme.account.exception.ErrorMessage.ACCOUNT_IS_CLOSED;
import static sme.account.exception.ErrorMessage.ACCOUNT_NOT_FOUND;
import static sme.account.exception.ErrorMessage.INSUFFICIENT_FUNDS;
import static sme.account.exception.ErrorMessage.INVALID_AMOUNT;
import static sme.account.exception.ErrorMessage.INVALID_CURRENCY;
import static sme.account.model.AccountOperation.CREDIT;
import static sme.account.model.AccountOperation.DEBIT;
import static sme.account.model.AccountStatus.CLOSED;
import static sme.account.util.CurrencyHelper.findCurrencyByNumericCode;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountStatus getStatus(Long accountNumber) {
        return findAccountByNumber(accountNumber).getStatus();
    }

    @Override
    public AccountBalanceResponse balanceInquiry(Long accountNumber) {
        return modelMapper.map(findAccountByNumber(accountNumber), AccountBalanceResponse.class);
    }

    @Override
    public void transfer(TransferOperationRequest request) {
        Account account = findAccountByNumber(request.accountNumber());
        validateTransferOperationRequest(request, account);
        if (CREDIT == request.operationType()) {
            account.setBalance(account.getBalance().subtract(request.amount()));
        }
        if (DEBIT == request.operationType()) {
            account.setBalance(account.getBalance().add(request.amount()));
        }
        accountRepository.save(account);
    }

    private Account findAccountByNumber(Long accountNumber) {
        return accountRepository.findAccountByNumber(accountNumber)
                .orElseThrow(ACCOUNT_NOT_FOUND::getException);
    }

    private void validateTransferOperationRequest(TransferOperationRequest request, Account account) {
        if (CLOSED == account.getStatus()) {
            ACCOUNT_IS_CLOSED.throwException();
        }
        if (findCurrencyByNumericCode(request.currency()) != account.getCurrency()) {
            INVALID_CURRENCY.throwException();
        }
        if (request.amount().compareTo(new BigDecimal(0)) < 0) {
            INVALID_AMOUNT.throwException();
        }
        if (CREDIT == request.operationType() && account.getBalance().compareTo(request.amount()) < 0) {
            INSUFFICIENT_FUNDS.throwException();
        }
    }

}

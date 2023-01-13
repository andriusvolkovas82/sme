package sme.account.model.response;

import sme.account.model.AccountStatus;

import java.math.BigDecimal;
import java.util.Currency;

public class AccountBalanceResponse {

    private Long number;
    private Currency currency;
    private BigDecimal balance;
    private AccountStatus status;

    public AccountBalanceResponse() {
    }

    public AccountBalanceResponse(Long number, Currency currency, BigDecimal balance, AccountStatus status) {
        this.number = number;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }


}
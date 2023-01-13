package sme.account.model.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import sme.account.model.AccountStatus;

import java.math.BigDecimal;
import java.util.Currency;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long number;

    @Convert(converter=CurrencyConverter.class)
    private Currency currency;

    private BigDecimal balance;

    @Enumerated(value = STRING)
    private AccountStatus status;

    public Account() {
    }

    public Long getNumber() {
        return number;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}

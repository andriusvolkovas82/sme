package sme.account.util;

import java.util.Currency;

import static sme.account.exception.ErrorMessage.INVALID_CURRENCY;

public class CurrencyHelper {

    public static Currency findCurrencyByNumericCode(Integer numericCode) {
        return Currency.getAvailableCurrencies().stream().filter(currency -> currency.getNumericCode() == numericCode)
                .findAny().orElseThrow(INVALID_CURRENCY::getException);
    }

}

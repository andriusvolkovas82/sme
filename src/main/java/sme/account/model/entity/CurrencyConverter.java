package sme.account.model.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Currency;

import static sme.account.util.CurrencyHelper.findCurrencyByNumericCode;

@Converter
public class CurrencyConverter implements AttributeConverter<Currency, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Currency currency) {
        return currency.getNumericCode();
    }

    @Override
    public Currency convertToEntityAttribute(Integer numericCode) {
        return findCurrencyByNumericCode(numericCode);
    }
}
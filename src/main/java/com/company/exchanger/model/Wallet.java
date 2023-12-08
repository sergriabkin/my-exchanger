package com.company.exchanger.model;

import com.company.exchanger.exception.NotEnoughMoneyException;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public final class Wallet {

    private List<Currency> balance;

    public Wallet(List<Currency> currencyList) {
        this.balance = new CopyOnWriteArrayList<>(currencyList);
    }

    public List<Currency> getBalance() {
        return new CopyOnWriteArrayList<>(balance);
    }

    public Wallet addTransaction(List<Currency> currencyList) {
        List<Currency> newBalance =
                Stream.concat(balance.stream(), currencyList.stream())
                        .collect(Collectors.groupingBy(Currency::getTicker))
                        .entrySet()
                        .stream()
                        .map(e -> sum(e.getValue(), e.getKey()))
                        .toList();
        checkNotNegative(newBalance);
        return new Wallet(newBalance);
    }

    private void checkNotNegative(List<Currency> newBalance) {
        Optional<Currency> negativeCurrency = newBalance.stream()
                .filter(c -> c.getAmount().signum() == -1)
                .findFirst();
        if (negativeCurrency.isPresent()){
            throw new NotEnoughMoneyException( "can't save transaction result: " + negativeCurrency.get());
        }
    }

    public Currency sum(List<Currency> list, Ticker ticker) {
        BigDecimal amount = list.stream()
                .filter(t -> Objects.equals(t.getTicker(), ticker))
                .map(Currency::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return Currency.builder().ticker(ticker).amount(amount).build();
    }

}

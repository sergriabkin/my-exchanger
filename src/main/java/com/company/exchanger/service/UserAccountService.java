package com.company.exchanger.service;

import com.company.exchanger.cache.UserAccountCache;
import com.company.exchanger.model.Currency;
import com.company.exchanger.model.UserAccount;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAccountService {

    private final UserAccountCache cache;

    public UserAccountService(UserAccountCache cache) {
        this.cache = cache;
    }

    public UserAccount get(String id){
        return cache.get(id);
    }

    public List<UserAccount> getAll() {
        return cache.getAll();
    }

    public UserAccount save(UserAccount account) {
        if (checkNegativeBalances(account)) {
            throw new IllegalArgumentException("Balance can't be negative");
        }
        return cache.save(account);
    }

    private boolean checkNegativeBalances(UserAccount account) {
        return account.getWallet().getBalance().stream()
                .collect(Collectors.groupingBy(Currency::getTicker))
                .entrySet().stream()
                .anyMatch(entry -> hasNegativeSum(entry.getValue()));
    }

    private boolean hasNegativeSum(List<Currency> value) {
        BigDecimal sum = value.stream()
                .map(Currency::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.compareTo(BigDecimal.ZERO) < 0;
    }

}

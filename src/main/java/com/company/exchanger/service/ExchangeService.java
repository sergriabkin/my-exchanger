package com.company.exchanger.service;

import com.company.exchanger.cache.ExchangeRateCache;
import com.company.exchanger.dto.ExchangeDto;
import com.company.exchanger.dto.ExchangeStatusDto;
import com.company.exchanger.dto.Status;
import com.company.exchanger.model.Currency;
import com.company.exchanger.model.ExchangeRate;
import com.company.exchanger.exception.NotEnoughMoneyException;
import com.company.exchanger.model.UserAccount;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class ExchangeService {

    private final ExchangeRateCache exchangeRateCache;
    private final UserAccountService userAccountService;

    public ExchangeService(ExchangeRateCache exchangeRateCache, UserAccountService userAccountService) {
        this.exchangeRateCache = exchangeRateCache;
        this.userAccountService = userAccountService;
    }

    public void addExchangeRate(ExchangeRate rate){
        exchangeRateCache.addRate(rate);
    }

    public ExchangeStatusDto exchange(ExchangeDto dto){
        try {
            UserAccount userAccount = userAccountService.get(dto.getUserId());
            List<Currency> currencies = getCurrenciesForTransaction(dto);
            ExchangeStatusDto statusDto = makeTransaction(userAccount, currencies);
            log.info(statusDto);
            return statusDto;
        } catch (RuntimeException e){
            return new ExchangeStatusDto(Status.FAIL, e.getMessage());
        }
    }

    private List<Currency> getCurrenciesForTransaction(ExchangeDto dto) {
        ExchangeRate exchangeRate = exchangeRateCache.getRate(dto.getFrom(), dto.getTo());

        Currency minusFrom = Currency.builder()
                .ticker(dto.getFrom())
                .amount(dto.getAmountFrom().negate())
                .build();

        Currency plusTo = Currency.builder()
                .ticker(dto.getTo())
                .amount(dto.getAmountFrom().multiply(exchangeRate.getRate()))
                .build();

        return List.of(minusFrom, plusTo);
    }

    private ExchangeStatusDto makeTransaction(UserAccount userAccount, List<Currency> currencies) {
        try {
            UserAccount updatedAccount = userAccount.addTransaction(currencies);
            log.info("Updated: " + userAccountService.save(updatedAccount));
            return new ExchangeStatusDto(currencies, Status.SUCCESS, "");
        } catch (NotEnoughMoneyException e){
            return new ExchangeStatusDto(currencies, Status.FAIL, e.getMessage());
        }
    }

}

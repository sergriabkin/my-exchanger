package com.company.exchanger;

import com.company.exchanger.dto.ExchangeDto;
import com.company.exchanger.dto.ExchangeStatusDto;
import com.company.exchanger.model.Currency;
import com.company.exchanger.model.ExchangeRate;
import com.company.exchanger.model.UserAccount;
import com.company.exchanger.model.Wallet;
import com.company.exchanger.service.ExchangeService;
import com.company.exchanger.service.UserAccountService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.company.exchanger.model.Ticker.*;

@Configuration
@Log4j2
public class StartRunner {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private ExchangeService exchangeService;

    @Bean
    public CommandLineRunner runner() {
        return (String... strings) -> {
            addRates();
            addUsers();
            performExchanges();
        };
    }

    private void addUsers() {
        UserAccount alis = userAccountService.save(getDemoAlis());
        log.info(userAccountService.get(alis.getId()));
        UserAccount bob = userAccountService.save(getDemoBob());
        log.info(userAccountService.get(bob.getId()));
    }

    @SneakyThrows
    private void performExchanges() {
        List<ExchangeDto> exchanges = userAccountService.getAll().stream()
                .flatMap(u -> Stream.of(
                        ExchangeDto.builder().from(BTC).to(USD).amountFrom(new BigDecimal("0.001")).userId(u.getId()).build(),
                        ExchangeDto.builder().from(USD).to(BTC).amountFrom(new BigDecimal("59.2")).userId(u.getId()).build()
                )).toList();

        List<Callable<ExchangeStatusDto>> tasks = IntStream.range(0, 100_000)
                .mapToObj(i -> exchanges)
                .flatMap(Collection::stream)
                .map(e -> (Callable<ExchangeStatusDto>) (() -> exchangeService.exchange(e)))
                .toList();

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        executorService.invokeAll(tasks);
    }

    private void addRates() {
        exchangeService.addExchangeRate(new ExchangeRate(USD, BTC, getReversed("59300")));
        exchangeService.addExchangeRate(new ExchangeRate(BTC, USD, new BigDecimal("59100")));
        exchangeService.addExchangeRate(new ExchangeRate(UAH, USD, getReversed("27.90")));
        exchangeService.addExchangeRate(new ExchangeRate(USD, UAH, new BigDecimal("27.80")));
    }

    private BigDecimal getReversed(String rate) {
        return BigDecimal.ONE.divide(new BigDecimal(rate), 10, RoundingMode.DOWN);
    }

    private UserAccount getDemoAlis() {
        Currency usd = Currency.builder()
                .ticker(USD)
                .amount(new BigDecimal("5132.35"))
                .build();

        Currency btc = Currency.builder()
                .ticker(BTC)
                .amount(new BigDecimal("0.015"))
                .build();

        return UserAccount.builder()
                .name("Alis")
                .wallet(new Wallet(List.of(usd, btc)))
                .build();
    }

    private UserAccount getDemoBob() {
        Currency usd = Currency.builder()
                .ticker(USD)
                .amount(new BigDecimal("7200.88"))
                .build();

        Currency btc = Currency.builder()
                .ticker(BTC)
                .amount(new BigDecimal("0.3186"))
                .build();

        return UserAccount.builder()
                .name("Bob")
                .wallet(new Wallet(List.of(usd, btc)))
                .build();
    }

}

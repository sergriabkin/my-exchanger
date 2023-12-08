package com.company.exchanger.cache;

import com.company.exchanger.model.ExchangeRate;
import com.company.exchanger.model.Ticker;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConcurrentExchangeRateCache implements ExchangeRateCache {

    private final Map<ImmutablePair<Ticker, Ticker>, ExchangeRate> exchangeRates = new ConcurrentHashMap<>();

    @Override
    public ExchangeRate getRate(Ticker from, Ticker to){
        ImmutablePair<Ticker, Ticker> pair = ImmutablePair.of(from, to);
        if (!exchangeRates.containsKey(pair)){
            throw new IllegalArgumentException("Can't find any rates for this pair: " + pair);
        }
        return exchangeRates.get(pair);
    }

    @Override
    public ExchangeRate addRate(ExchangeRate rate){
        exchangeRates.put(ImmutablePair.of(rate.getFrom(), rate.getTo()), rate);
        return rate;
    }

}

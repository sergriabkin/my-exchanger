package com.company.exchanger.cache;

import com.company.exchanger.model.ExchangeRate;
import com.company.exchanger.model.Ticker;

public interface ExchangeRateCache {
    ExchangeRate getRate(Ticker from, Ticker to);

    ExchangeRate addRate(ExchangeRate rate);
}

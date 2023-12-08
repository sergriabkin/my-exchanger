package com.company.exchanger.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public final class ExchangeRate {
    private final Ticker from;
    private final Ticker to;
    private final BigDecimal rate;
}

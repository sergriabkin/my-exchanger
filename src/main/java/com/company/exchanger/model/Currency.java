package com.company.exchanger.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public final class Currency {
    private final Ticker ticker;
    private final BigDecimal amount;
}

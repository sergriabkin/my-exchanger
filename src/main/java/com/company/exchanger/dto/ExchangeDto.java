package com.company.exchanger.dto;

import com.company.exchanger.model.Ticker;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public final class ExchangeDto {
    private final String userId;
    private final Ticker from;
    private final Ticker to;
    private final BigDecimal amountFrom;
}

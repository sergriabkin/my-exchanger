package com.company.exchanger.dto;

import com.company.exchanger.model.Currency;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExchangeStatusDto {
    private final List<Currency> currencies;
    private final Status status;
    private final String message;

    public ExchangeStatusDto(List<Currency> currencies, Status status, String message) {
        this.currencies = currencies;
        this.status = status;
        this.message = message;
    }

    public ExchangeStatusDto(Status status, String message) {
        this.currencies = new ArrayList<>();
        this.status = status;
        this.message = message;
    }
}

package com.company.exchanger.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public final class UserAccount {

    private final String id;
    private final String name;
    private final Wallet wallet;

    public UserAccount addId(String id){
        return UserAccount.builder()
                .id(id)
                .name(name)
                .wallet(wallet)
                .build();
    }

    public UserAccount addTransaction(List<Currency> transaction){
        return UserAccount.builder()
                .id(id)
                .name(name)
                .wallet(wallet.addTransaction(transaction))
                .build();
    }

}

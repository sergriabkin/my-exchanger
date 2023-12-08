package com.company.exchanger.cache;

import com.company.exchanger.model.UserAccount;

import java.util.List;

public interface UserAccountCache {
    UserAccount save(UserAccount account);

    UserAccount get(String id);

    List<UserAccount> getAll();

}

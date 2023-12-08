package com.company.exchanger.dao;

import com.company.exchanger.model.UserAccount;

import java.util.List;

public interface UserAccountDao {

    UserAccount save(UserAccount account);

    UserAccount get(String id);

    List<UserAccount> getAll();
}

package com.company.exchanger.cache;

import com.company.exchanger.dao.UserAccountDao;
import com.company.exchanger.model.UserAccount;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class ConcurrentUserAccountCache implements UserAccountCache {

    private final UserAccountDao dao;

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    public ConcurrentUserAccountCache(UserAccountDao dao) {
        this.dao = dao;
    }

    @PostConstruct
    public void init() {
        dao.getAll().forEach(account -> accounts.put(account.getId(), account));
    }

    @PreDestroy
    public void destroy() {
        saveAllToDao();
    }

    @Scheduled(cron = "*/5 * * * * *")
    private void saveAllToDao() {
        accounts.values().forEach(dao::save);
    }


    @Override
    public UserAccount save(UserAccount account) {
        if (Objects.isNull(account.getId())) {
            account = dao.save(account);
        }
        accounts.put(account.getId(), account);
        return account;
    }

    @Override
    public UserAccount get(String id) {
        return accounts.get(id);
    }

    @Override
    public List<UserAccount> getAll() {
        return new ArrayList<>(accounts.values());
    }

}

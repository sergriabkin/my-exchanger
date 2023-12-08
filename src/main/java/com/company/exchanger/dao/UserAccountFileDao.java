package com.company.exchanger.dao;

import com.company.exchanger.exception.FileNotExistsException;
import com.company.exchanger.model.UserAccount;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class UserAccountFileDao implements UserAccountDao {

    private final String folderPath;

    public UserAccountFileDao(@Value("${folder.path}") String folderPath) {
        this.folderPath = folderPath;
    }

    @PostConstruct
    public void init(){
        clean();
    }

    @SneakyThrows
    private void clean() {
        Files.list(Paths.get(folderPath))
                .forEach(this::deleteFile);
    }

    private void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @SneakyThrows
    public UserAccount save(UserAccount account) {
        Path path = Paths.get(getAccountFileName(account.getId()));
        if (Files.exists(path)) {
            saveToFile(account, path);
            return account;
        } else {
            return create(account);
        }
    }

    private void saveToFile(UserAccount account, Path path) throws IOException {
        Files.writeString(path, new Gson().toJson(account));
        log.info(String.format("%n Account: %s %n was saved to file: %s", account, path));
    }

    @SneakyThrows
    private UserAccount create(UserAccount account) {
        final UserAccount accountToSave = account.addId(UUID.randomUUID().toString());
        Path path = Paths.get(getAccountFileName(accountToSave.getId()));
        Files.createFile(path);
        saveToFile(accountToSave, path);
        return accountToSave;
    }

    @Override
    @SneakyThrows
    public UserAccount get(String id) {
        Path path = Paths.get(getAccountFileName(id));
        if (Files.exists(path)) {
            return deserializeUserAccount(path);
        } else {
            throw new FileNotExistsException(id);
        }
    }

    @Override
    @SneakyThrows
    public List<UserAccount> getAll() {
        return Files.list(Paths.get(folderPath))
                .filter(Files::exists)
                .map(this::deserializeUserAccount)
                .toList();
    }

    private UserAccount deserializeUserAccount(Path path) {
        String json = getJson(path);
        UserAccount userAccount = new Gson().fromJson(json, UserAccount.class);
        log.info("Deserialized account: " + userAccount);
        return userAccount;
    }

    @SneakyThrows
    private String getJson(Path path) {
        return Files.readString(path);
    }

    private String getAccountFileName(String id) {
        return folderPath + id + ".txt";
    }

}

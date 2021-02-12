package com.nextplugins.economy.storage;

import com.google.common.collect.Maps;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.AccountDAO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class AccountStorage {

    @Getter private final Map<UUID, Account> accounts = Maps.newLinkedHashMap();

    private final AccountDAO accountDAO;

    public void init() {
        accountDAO.createTable();
    }

    public Account getByName(UUID owner) {

        Account account = accounts.getOrDefault(owner, null);
        if (account == null) {

            account = accountDAO.selectOne(owner);

            if (account == null) {

                account = Account.builder()
                        .owner(owner)
                        .balance(0)
                        .build();

                accountDAO.insertOne(account);
            }

            accounts.put(owner, account);
        }

        return account;
    }

    public void insertOne(Account account) {
        addAccount(account);
        accountDAO.insertOne(account);
    }

    public void deleteOne(Account account) {
        removeAccount(account);
        accountDAO.deleteOne(account);
    }

    public void purge(UUID owner) {
        Account account = accounts.getOrDefault(owner, null);

        if (account == null) return;

        accountDAO.saveOne(account);
        accounts.remove(account.getOwner());
    }

    public void addAccount(Account account) {
        if (!accounts.containsKey(account.getOwner())) {
            accounts.put(account.getOwner(), account);
        }
    }

    public void removeAccount(Account account) {
        accounts.put(account.getOwner(), account);
    }

}

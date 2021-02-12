package com.nextplugins.economy.task;

import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.storage.AccountStorage;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public final class AccountSaveTask implements Runnable {

    private final AccountStorage accountStorage;
    private final AccountDAO accountDAO;

    @Override
    public void run() {
        Collection<Account> accounts = accountStorage.getAccounts().values();

        if (!accounts.isEmpty()) {
            for (Account account : accountStorage.getAccounts().values()) {
                accountDAO.saveOne(account);
            }
        }

    }

}

package com.nextplugins.economy.task;

import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public final class AccountSaveTask implements Runnable {

    private final AccountStorage accountStorage;
    private final AccountRepository accountRepository;

    @Override
    public void run() {
        Collection<Account> accounts = accountStorage.getAccounts().values();

        if (!accounts.isEmpty()) {
            for (Account account : accountStorage.getAccounts().values()) {
                accountRepository.saveOne(account);
            }
        }

    }

}

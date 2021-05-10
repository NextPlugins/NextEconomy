package com.nextplugins.economy.task;

import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public final class AsyncAccountSaveTask implements Runnable {

    private final AccountStorage accountStorage;
    private final AccountRepository accountRepository;

    @Override
    public void run() {

        val accounts = accountStorage.getAccounts().values();
        if (accounts.isEmpty()) return;

        for (Account account : accounts) {
            accountRepository.saveOne(account);
        }

    }

}

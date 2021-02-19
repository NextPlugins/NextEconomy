package com.nextplugins.economy.task;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.dao.AccountDAO;
import com.nextplugins.economy.storage.RankingStorage;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class AccountRankingTask implements Runnable {

    private final AccountDAO accountDAO;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        List<Account> accounts = Lists.newLinkedList(accountDAO.selectAll("ORDER BY balance DESC LIMIT 10"));

        if (!accounts.isEmpty()) {
            rankingStorage.getRankingAccounts().clear();
            accounts.forEach(rankingStorage.getRankingAccounts()::add);
        }

    }

}

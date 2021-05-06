package com.nextplugins.economy.ranking.storage;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;

import java.util.List;

@Data
public final class RankingStorage {

    private final List<Account> rankByCoin = Lists.newLinkedList();
    private final List<Account> rankByMovimentation = Lists.newLinkedList();
    private long nextUpdateMillis = System.currentTimeMillis();

}

package com.nextplugins.economy.storage;

import com.google.common.collect.Maps;
import com.nextplugins.economy.api.model.Account;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.UUID;

@Data
public final class RankingStorage {

    private final LinkedHashMap<UUID, Double> rankingAccounts = Maps.newLinkedHashMap();

    public void addAccount(Account account) {
        rankingAccounts.put(account.getOwner(), account.getBalance());
    }

}

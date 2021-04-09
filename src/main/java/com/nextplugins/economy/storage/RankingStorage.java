package com.nextplugins.economy.storage;

import com.google.common.collect.Lists;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;

import java.util.List;

@Data
public final class RankingStorage {

    private final List<Account> rankingAccounts = Lists.newLinkedList();

}

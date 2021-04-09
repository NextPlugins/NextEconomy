package com.nextplugins.economy.storage;

import com.google.common.collect.Maps;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.AccountDAO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
public final class AccountStorage {

    @Getter private final Map<String, Account> accounts = Maps.newLinkedHashMap();

    @Getter private final AccountDAO accountDAO;

    public void init() {

        accountDAO.createTable();
        NextEconomy.getInstance().getLogger().info("DAO do plugin iniciado com sucesso.");

    }

    /**
     * Used to no cache account
     *
     * @param userName player name
     * @return {@link Account} found
     */
    @Nullable
    public Account findOfflineAccount(String userName) {

        Account account = accounts.getOrDefault(userName, null);
        if (account == null) account = accountDAO.selectOne(userName);

        return account;

    }

    public Account findOnlineAccount(Player player) {

        String userName = player.getName();
        Account account = accounts.getOrDefault(userName, null);
        if (account == null) {

            account = accountDAO.selectOne(userName);

            if (account == null) {

                account = Account.createDefault(userName);
                accountDAO.saveOne(account);

            }

            accounts.put(userName, account);
        }

        return account;
    }

    public void insertOne(Account account) {
        addAccount(account);
        accountDAO.saveOne(account);
    }

    public void deleteOne(Account account) {
        removeAccount(account);
        accountDAO.deleteOne(account);
    }

    public void purge(String name) {

        Account account = accounts.getOrDefault(name, null);
        if (account == null) return;

        accountDAO.saveOne(account);
        removeAccount(account);
    }

    public void addAccount(Account account) {
        if (!accounts.containsKey(account.getUserName())) {
            accounts.put(account.getUserName(), account);
        }
    }

    public void removeAccount(Account account) {
        accounts.put(account.getUserName(), account);
    }

}

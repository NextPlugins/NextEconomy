package com.nextplugins.economy.model.account.storage;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.model.account.Account;
import com.nextplugins.economy.model.account.transaction.TransactionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public final class AccountStorage {

    private final AccountRepository accountRepository;
    private boolean nickMode;
    private int depositCount;
    private int withdrawCount;

    private final AsyncLoadingCache<String, Account> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .evictionListener((RemovalListener<String, Account>) (key, value, cause) -> {
                if (value == null) return;
                saveOne(value);
            })
            .removalListener((key, value, cause) -> {
                if (value == null) return;
                saveOne(value);
            })
            .buildAsync((key, executor) -> CompletableFuture.completedFuture(selectOne(key)));

    public void init(boolean nickMode) {
        this.nickMode = nickMode;
        accountRepository.createTable();

        NextEconomy.getInstance().getLogger().info("DAO do plugin iniciado com sucesso.");
    }

    public void increaseTransactionCount(TransactionType transactionType) {
        if (transactionType == TransactionType.DEPOSIT) depositCount++;
        else withdrawCount++;
    }

    /**
     * Save account in repository
     *
     * @param account to save
     */
    public void saveOne(@NotNull Account account) {
        accountRepository.saveOne(account);
    }

    public void fastSaveOne(@NotNull String identifier, double balance) {
        accountRepository.updateOne(identifier, balance);
    }

    /**
     * Find a user in repository
     *
     * @param owner uuid or nick of user
     * @return {@link Account} found
     */
    @Nullable
    private Account selectOne(@NotNull String owner) {
        return accountRepository.selectOne(owner);
    }

    /**
     * Used to get created accounts by name
     *
     * @param name player name
     * @return {@link Account} found
     */
    @Nullable
    public Account findAccountByName(@NotNull String name) {
        try {
            return cache.get(name).get();
        } catch (InterruptedException | ExecutionException exception) {
            Thread.currentThread().interrupt();
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * Used to get accounts
     * If player is online and no have account, we will create a new for them
     * but, if is offline, will return null
     *
     * @param offlinePlayer player
     * @return {@link Account} found
     */
    @Nullable
    public Account findAccount(@NotNull OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            val player = offlinePlayer.getPlayer();
            if (player != null) return findAccount(player);
        }

        if (nickMode && offlinePlayer.getName() == null) return null;
        return findAccountByName(nickMode ? offlinePlayer.getName() : offlinePlayer.getUniqueId().toString());
    }

    /**
     * Used to get accounts
     *
     * @param player player to search
     * @return {@link Account} found
     */
    @NotNull
    public Account findAccount(@NotNull Player player) {
        Account account = findAccountByName(nickMode ? player.getName() : player.getUniqueId().toString());
        if (account == null) {
            account = Account.createDefault(player);
            put(account);
            saveOne(account);
        }

        // update username if player change (original users)
        if (nickMode && !account.getUsername().equalsIgnoreCase(player.getName())) {
            account.setUsername(player.getName());
        }

        return account;
    }

    /**
     * Put account directly in cache (will be sync to database automaticly)
     *
     * @param account of player
     */
    public void put(@NotNull Account account) {
        cache.put(account.getUsername(), CompletableFuture.completedFuture(account));
    }

    public void fastFlushData() {
        val accountMap = cache.asMap();

        for (val futureAccount : accountMap.values()) {
            futureAccount.whenCompleteAsync((account, t) -> account.fastSave());
        }
    }

    /**
     * Flush data from cache
     */
    public void flushData() {
        val accountMap = cache.asMap();

        for (val futureAccount : accountMap.values()) {
            futureAccount.whenCompleteAsync((account, t) -> account.save());
        }
    }

}

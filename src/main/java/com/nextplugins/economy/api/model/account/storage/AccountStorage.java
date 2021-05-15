package com.nextplugins.economy.api.model.account.storage;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class AccountStorage {

    @Getter private final AccountRepository accountRepository;

    @Getter private final AsyncLoadingCache<String, Account> cache = Caffeine.newBuilder()
            .ticker(System::nanoTime)
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .removalListener(this::saveOne)
            .buildAsync(this::selectOne);

    public void init() {

        accountRepository.createTable();
        NextEconomy.getInstance().getLogger().info("DAO do plugin iniciado com sucesso.");

    }

    private void saveOne(String name, Account account, @NonNull RemovalCause removalCause) {
        accountRepository.saveOne(account);
    }

    private @NotNull CompletableFuture<Account> selectOne(String s, @NonNull Executor executor) {
        return CompletableFuture.completedFuture(accountRepository.selectOne(s));
    }

    /**
     * Used to get created accounts by name
     *
     * @param name player name
     * @return {@link Account} found
     */
    @Nullable
    public Account findAccountByName(String name) {

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

        if (offlinePlayer.isOnline()) return findAccount(offlinePlayer.getPlayer());
        return findAccountByName(offlinePlayer.getName());

    }

    /**
     * Used to get accounts
     *
     * @param player player to search
     * @return {@link Account} found
     */
    @NotNull
    public Account findAccount(@NotNull Player player) {

        var account = findAccountByName(player.getName());
        if (account == null) {

            account = Account.createDefault(player.getName());
            put(account);

        }

        return account;

    }

    public void put(Account account) {
        cache.put(account.getUserName(), CompletableFuture.completedFuture(account));
    }

}

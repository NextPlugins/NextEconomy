package com.nextplugins.economy.api.model.account.storage;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class AccountStorage {

    @Getter private final AccountRepository accountRepository;

    @Getter private final AsyncLoadingCache<String, Account> CACHE = Caffeine.newBuilder()
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
     * Used to no cache account
     *
     * @param username player name
     * @return {@link Account} found
     */
    public Account findOfflineAccount(String username) {
        try {
            return CACHE.get(username).get();
        } catch (Exception ignored) {
            return null;
        }
    }

    public Account findOnlineAccount(Player player) {

        val username = player.getName();
        var account = findOfflineAccount(username);

        if (account == null) {

            account = Account.createDefault(username);
            accountRepository.saveOne(account);

            CACHE.put(player.getName(), account);

        }

        return account;
    }

}

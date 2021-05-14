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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

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
     * Used to get accounts
     *
     * @param username player name
     * @param online if player is online, can't return a null account
     * @return {@link Account} found
     */
    public Account findAccount(String username, boolean online) {
        try {

            var account = cache.get(username).get();
            if (account == null && online) {

                account = Account.createDefault(username);
                put(account);

            }

            return account;

        } catch (InterruptedException | ExecutionException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void put(Account account) {
        cache.put(account.getUserName(), CompletableFuture.completedFuture(account));
    }

}

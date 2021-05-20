package com.nextplugins.economy.api;

import com.google.common.collect.Sets;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NextEconomyAPI {

    /**
     * Only for cached accounts
     * Access {@link AccountRepository} to make operations direct to/from sql
     */

    @Getter private static final NextEconomyAPI instance = new NextEconomyAPI();

    private final AccountRepository accountRepository = NextEconomy.getInstance().getAccountRepository();
    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();
    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    /**
     * Search player in cache and sql
     * Can be null if player not exists in database & is offline
     * If player is online, the return value can't be null
     *
     * @param player an online player
     * @return {@link Account} the account found
     */
    public @Nullable Account findAccountByPlayer(@NotNull OfflinePlayer player) {
        return accountStorage.findAccount(player);
    }

    /**
     * Search player in cache and sql
     * Can be null if player not exists in database
     *
     * @param name player name
     * @return {@link Account} the account found
     */
    public @Nullable Account findAccountByName(@NotNull String name) {
        return accountStorage.findAccountByName(name);
    }

    /**
     * Retrieve all accounts loaded in cache.
     *
     * @return {@link Collection} with accounts
     */
    public @NotNull Collection<CompletableFuture<Account>> retrieveCachedAccounts() {
        return accountStorage.getCache().asMap().values();
    }

    /**
     * Search all accounts in cache to look for one with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Stream} aplicated with filter
     * @deprecated Since 2.0.0
     */
    public @Deprecated @NotNull Stream<Account> findAccountByFilter(@NotNull Predicate<Account> filter) {
        return retrieveCachedAccounts().stream()
                .map(future -> {

                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException exception) {
                        Thread.currentThread().interrupt();
                        return null;
                    }

                })
                .filter(filter);
    }

    /**
     * Retrieve all accounts loaded in cache.
     *
     * @return {@link Set} with accounts
     * @deprecated Since 2.0.0
     */
    public @Deprecated @NotNull Set<CompletableFuture<Account>> allAccounts() {
        return Sets.newHashSet(retrieveCachedAccounts());
    }

}

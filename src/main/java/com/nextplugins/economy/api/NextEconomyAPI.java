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
import org.jetbrains.annotations.Nullable;

import java.util.Set;
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
     * Search all accounts in cache to look for one with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link Stream} aplicated with filter
     */
    public Stream<Account> findAccountByFilter(Predicate<Account> filter) {
        return allAccounts().stream()
                .filter(filter);
    }

    /**
     * Search player in cache and sql
     * Can be null if player not exists in database
     *
     * @param player an online player
     * @return {@link Account} the account found
     */
    @Nullable
    public Account findAccountByPlayer(OfflinePlayer player) {
        return findAccountByName(player.getName());
    }

    /**
     * Search player in cache and sql
     * Can be null if player not exists in database
     *
     * @param name player name
     * @return {@link Account} the account found
     */
    @Nullable
    public Account findAccountByName(String name) {
        return accountStorage.findOfflineAccount(name);
    }

    /**
     * Retrieve all accounts loaded in cache.
     *
     * @return {@link java.util.Set} with accounts
     */
    public Set<Account> allAccounts() {
        return Sets.newLinkedHashSet(accountStorage.getCache().synchronous().asMap().values());
    }

}

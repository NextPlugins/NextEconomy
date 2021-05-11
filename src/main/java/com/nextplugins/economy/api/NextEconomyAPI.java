package com.nextplugins.economy.api;

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

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
     *
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
     * @return {@link java.util.Collection} with accounts
     */
    public Collection<CompletableFuture<Account>> allAccounts() {
        return accountStorage.getCache().asMap().values();
    }

}

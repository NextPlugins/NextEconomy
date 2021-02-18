package com.nextplugins.economy.api;

import com.google.common.collect.Sets;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.storage.AccountStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NextEconomyAPI {

    @Getter private static final NextEconomyAPI instance = new NextEconomyAPI();

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    /**
     * Search all accounts to look for one with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link java.util.Optional} with the account found
     */
    public Optional<Account> findAccountByFilter(Predicate<Account> filter) {
        return allAccounts().stream()
                .filter(filter)
                .findFirst();
    }

    /**
     * Search all accounts to look for every with the entered custom filter.
     *
     * @param filter custom filter to search
     * @return {@link java.util.Set} with all accounts found
     */
    public Set<Account> findAccountsByFilter(Predicate<Account> filter) {
        return allAccounts().stream()
                .filter(filter)
                .collect(Collectors.toSet());
    }

    /**
     * Search all accounts to look for every with the entered custom filter.
     *
     * @param owner account owner name
     * @return {@link java.util.Optional} with the account found
     */
    public Optional<Account> findAccountByUUID(UUID owner) {
        return allAccounts().stream()
                .filter(account -> account.getOwner().equals(owner))
                .findFirst();
    }

    /**
     * Search all accounts to look for every with the entered custom filter.
     *
     * @param player an online player
     * @return {@link java.util.Optional} with the account found
     */
    public Optional<Account> findAccountByPlayer(Player player) {
        return allAccounts().stream()
                .filter(account -> account.getOwner().equals(player.getUniqueId()))
                .findFirst();
    }

    public Optional<Account> findAccountByName(String name) {
        return allAccounts().stream()
                .filter(account -> Bukkit.getOfflinePlayer(account.getOwner()).getName().equals(name))
                .findFirst();
    }

    /**
     * Retrieve all accounts loaded so far.
     *
     * @return {@link java.util.Set} with accounts
     */
    public Set<Account> allAccounts() {
        return Sets.newLinkedHashSet(accountStorage.getAccounts().values());
    }

}

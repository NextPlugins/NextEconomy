package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.model.account.Account;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.ranking.RankingStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class EconomyPlaceholder extends PlaceholderExpansion {

    private final NextEconomy plugin;
    private final AccountStorage accountStorage;


    public EconomyPlaceholder() {
        this.plugin = NextEconomy.getInstance();
        this.accountStorage = plugin.getAccountStorage();
    }

    public AccountStorage getAccountStorage() {
        return accountStorage;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nexteconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NextPlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        final Account account = accountStorage.findAccount(player);

        if (params.equalsIgnoreCase("amount")) {
            return account.getBalanceFormatted();
        }

        final RankingStorage rankingStorage = plugin.getRankingStorage();

        if (params.equalsIgnoreCase("tycoon")) {
            return rankingStorage.getTycoonTag(player.getName());
        } else if (params.equalsIgnoreCase("tycoon_name")) {
            return rankingStorage.getTopPlayer();
        }

        return "Placeholder inválida";
    }

}

package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class EconomyPlaceholderHook extends PlaceholderExpansion {

    private final NextEconomy plugin;
    private final boolean useTycoon = RankingValue.get(RankingValue::useTycoonTag);

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
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
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "&cOcorreu um erro!";

        final Account account = NextEconomyAPI.getInstance().findAccountByPlayer(player);
        final String balance = account == null ? "&cOcorreu um erro!" : NumberUtils.format(account.getBalance());

        if (params.equalsIgnoreCase("amount")) {
            return balance;
        }

        if (params.equalsIgnoreCase("tycoon") && useTycoon) {

            val rankByCoin = NextEconomyAPI.getInstance().getRankingStorage().getRankByCoin();
            if (rankByCoin.isEmpty()) return "";

            val topAccount = rankByCoin.get(0);
            if (!topAccount.getUserName().equals(player.getName())) return "";

            return RankingValue.get(RankingValue::tycoonTagValue);

        }

        if (params.equalsIgnoreCase("purse")) {
            return PurseAPI.getInstance().getPurseFormated();
        }

        if (params.equalsIgnoreCase("purse_only_value")) {
            return String.valueOf(PurseAPI.getInstance().getPurse());
        }

        if (params.equalsIgnoreCase("purse_with_icon")) {
            return PurseAPI.getInstance().getPurseFormatedWithIcon();
        }

        return "Placeholder inválida";
    }

}

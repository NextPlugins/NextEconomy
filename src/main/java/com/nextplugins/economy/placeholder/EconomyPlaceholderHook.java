package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.configuration.RankingValue;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class EconomyPlaceholderHook extends PlaceholderExpansion {

    private final NextEconomy plugin;
    private final PurseAPI instance = PurseAPI.getInstance();

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
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {

        val account = NextEconomyAPI.getInstance().findAccountByPlayer(player);
        if (account == null) return "&cOcorreu um erro!";

        if (params.equalsIgnoreCase("amount")) {
            return account.getBalanceFormated();
        }

        if (params.equalsIgnoreCase("tycoon")) {

            val rankingStorage = NextEconomyAPI.getInstance().getRankingStorage();
            val tycoonAccount = rankingStorage.getRankByCoin().get(0);

            return player.getName().equalsIgnoreCase(tycoonAccount.getUsername())
                    ? RankingValue.get(RankingValue::tycoonTagValue)
                    : RankingValue.get(RankingValue::tycoonRichTagValue);

        }

        if (instance != null) {

            if (params.equalsIgnoreCase("purse_with_icon")) return instance.getPurseFormatedWithIcon();
            else return instance.getPurseFormated();

        }

        return "Placeholder inválida";
    }

}

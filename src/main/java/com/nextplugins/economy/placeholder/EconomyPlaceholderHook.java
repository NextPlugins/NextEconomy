package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.PurseAPI;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class EconomyPlaceholderHook extends PlaceholderExpansion {

    private final NextEconomy plugin;

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
    public String onPlaceholderRequest(Player player, String params) {
        if (params.startsWith("purse")) {
            val purse = PurseAPI.getInstance();
            if (purse == null) return "Bolsa Desativada";

            if (params.equalsIgnoreCase("purse")) return purse.getPurseFormated();
            else if (params.equalsIgnoreCase("purse_only_value")) return String.valueOf(purse.getPurse());
            else return purse.getPurseFormatedWithIcon();
        }

        val account = plugin.getAccountStorage().findAccount(player);
        if (params.equalsIgnoreCase("amount")) {
            return account.getBalanceFormated();
        }

        val rankingStorage = plugin.getRankingStorage();

        if (params.equalsIgnoreCase("tycoon")) {
            return rankingStorage.getTycoonTag(player.getName());
        }

        if (params.equalsIgnoreCase("tycoon_name")) {
            return rankingStorage.getTopPlayer();
        }

        return "Placeholder inválida";
    }

}

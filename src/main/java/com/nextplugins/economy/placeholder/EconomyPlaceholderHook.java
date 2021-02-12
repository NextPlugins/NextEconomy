package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.util.NumberFormat;
import lombok.RequiredArgsConstructor;
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
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "&cOcorreu um erro!";

        final Account account = NextEconomyAPI.getInstance().findAccountByPlayer(player).orElse(null);
        final String balance = account == null ? "&cOcorreu um erro!" : NumberFormat.format(account.getBalance());

        if (params.equalsIgnoreCase("amount")) {
            return balance;
        }

        return "";
    }

}
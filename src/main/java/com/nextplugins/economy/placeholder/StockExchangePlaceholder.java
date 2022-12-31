package com.nextplugins.economy.placeholder;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.StockExchangeAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class StockExchangePlaceholder extends PlaceholderExpansion {

    private final StockExchangeAPI api;

    public StockExchangePlaceholder() {
        this.api = StockExchangeAPI.getInstance();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "nexteconomy_stock_exchange";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NextPlugins";
    }

    @Override
    public @NotNull String getVersion() {
        return NextEconomy.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (!api.isEnabled()) return "Função desativada.";

        switch (params.toLowerCase().trim()) {
            case "value": return api.getValueFormatted();
            case "value_raw": return String.valueOf(api.getValue());
            case "value_custom": return api.getValueAsPercentage();
            default: return "Placeholder inválida.";
        }
    }
}

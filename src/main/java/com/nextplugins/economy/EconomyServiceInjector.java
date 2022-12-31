package com.nextplugins.economy;

import com.nextplugins.economy.EconomyHook;
import com.nextplugins.economy.NextEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public final class EconomyServiceInjector {

    private EconomyServiceInjector() {}

    public static void inject() {
        final NextEconomy plugin = NextEconomy.getInstance();

        Bukkit.getServicesManager().register(
                Economy.class,
                new EconomyHook(),
                plugin,
                ServicePriority.Highest
        );

        plugin.getLogger().info("Associação com Vault feita com sucesso.");
    }

}

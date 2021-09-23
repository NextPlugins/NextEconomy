package com.nextplugins.economy.vault.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.vault.VaultEconomyHook;
import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Data(staticConstructor = "of")
public final class VaultHookRegistry {

    private final NextEconomy plugin;

    public VaultHookRegistry register() {
        Bukkit.getServer().getServicesManager()
                .register(
                        Economy.class,
                        new VaultEconomyHook(),
                        plugin,
                        ServicePriority.Highest
                );

        getPlugin().getLogger().info("Associação com o 'Vault' realizada com sucesso.");
        return this;
    }

}

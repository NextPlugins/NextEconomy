package com.nextplugins.economy.vault.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.vault.VaultEconomyHook;
import com.nextplugins.economy.vault.VaultGroupHook;
import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Data(staticConstructor = "of")
public final class VaultHookRegistry {

    private final NextEconomy plugin;
    private final VaultGroupHook vaultGroupHook = new VaultGroupHook();

    public VaultHookRegistry register() {
        Bukkit.getServer().getServicesManager()
                .register(
                        Economy.class,
                        new VaultEconomyHook(),
                        plugin,
                        ServicePriority.Highest
                );

        this.vaultGroupHook.init(plugin);
        getPlugin().getLogger().info("Associação com o 'Vault' realizada com sucesso.");

        return this;
    }

}

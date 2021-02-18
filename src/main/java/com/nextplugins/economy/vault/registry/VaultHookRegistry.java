package com.nextplugins.economy.vault.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.vault.VaultExpansionHook;
import lombok.Data;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Data(staticConstructor = "of")
public final class VaultHookRegistry {

    private final NextEconomy plugin;

    public void register() {
        Bukkit.getServer().getServicesManager().register(Economy.class, new VaultExpansionHook(), plugin, ServicePriority.Highest);
    }

}

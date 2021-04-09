package com.nextplugins.economy.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.inventory.MainInventory;
import com.nextplugins.economy.inventory.RankingInventory;
import lombok.Getter;

@Getter
public final class InventoryRegistry {

    @Getter private static final InventoryRegistry instance = new InventoryRegistry();

    private MainInventory mainInventory;
    private RankingInventory rankingInventory;

    public void init(NextEconomy plugin) {

        this.mainInventory = new MainInventory(plugin.getAccountStorage()).init();
        this.rankingInventory = new RankingInventory().init();

    }

}

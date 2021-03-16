package com.nextplugins.economy.inventory;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.nextplugins.economy.configuration.values.InventoryValue;
import com.nextplugins.economy.inventory.button.InventoryButton;
import com.nextplugins.economy.registry.InventoryButtonRegistry;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MainInventory extends SimpleInventory {

    private final InventoryButtonRegistry buttons = InventoryButtonRegistry.getInstance();

    public MainInventory() {
        super(
                "nexteconomy.main",
                InventoryValue.get(InventoryValue::mainInventoryName),
                3 * 9
        );
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        Player player = viewer.getPlayer();

        InventoryButton yourMoney = buttons.get("main.yourMoney");
        editor.setItem(yourMoney.getInventorySlot(), InventoryItem.of(yourMoney.getItemStack(player)));

    }
}

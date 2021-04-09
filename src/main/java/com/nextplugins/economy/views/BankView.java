package com.nextplugins.economy.views;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.values.InventoryValue;
import com.nextplugins.economy.views.button.InventoryButton;
import com.nextplugins.economy.registry.InventoryButtonRegistry;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class BankView extends SimpleInventory {

    private static final InventoryButtonRegistry BUTTONS = InventoryButtonRegistry.getInstance();

    private final AccountStorage accountStorage;

    public BankView(AccountStorage accountStorage) {
        super(
                "nexteconomy.main",
                InventoryValue.get(InventoryValue::mainInventoryName),
                InventoryValue.get(InventoryValue::mainInventorySize)
        );

        this.accountStorage = accountStorage;
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        Player player = viewer.getPlayer();
        Account account = accountStorage.findOnlineAccount(player);

        String purse = PurseAPI.getInstance() != null
                ? PurseAPI.getInstance().getPurse() + "%"
                : "";

        String isHigh = PurseAPI.getInstance() != null
                ? PurseAPI.getInstance().isHigh()
                : "";

        for (InventoryButton value : BUTTONS.values()) {

            int inventorySlot = value.getInventorySlot();
            if (inventorySlot == -1) continue;

            ItemStack valueItem = new ItemBuilder(value.getItemStack(player))
                    .setLore(value.getLore()
                            .stream()
                            .map(line -> line
                                    .replace("$money", NumberUtils.format(account.getBalance()))
                                    .replace("$transactions", String.valueOf(account.getTransactions()))
                                    .replace("$movimentedMoney", NumberUtils.format(account.getMovimentedBalance()))
                                    .replace("$value", purse)
                                    .replace("$status", isHigh)
                            )
                            .collect(Collectors.toList())
                    ).wrap();

            editor.setItem(
                    inventorySlot,
                    InventoryItem.of(valueItem).defaultCallback(value.getButtonType().getAction())
            );


        }

    }

}

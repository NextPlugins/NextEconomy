package com.nextplugins.economy.views;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.views.button.registry.InventoryButtonRegistry;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import com.nextplugins.economy.util.TimeUtils;
import com.nextplugins.economy.views.button.InventoryButton;
import lombok.val;
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

        val player = viewer.getPlayer();
        val account = accountStorage.findOnlineAccount(player);
        val instance = PurseAPI.getInstance();

        String purse = instance != null ? instance.getPurseFormated() : "";

        String isHigh = instance != null ? instance.isHigh() : "";

        String nextUpdate = instance != null
                ? TimeUtils.format(instance.getNextUpdate() - System.currentTimeMillis())
                : "";

        String transactionName = account.getTransactionsQuantity() == 1
                ? MessageValue.get(MessageValue::singularTransaction)
                : MessageValue.get(MessageValue::pluralTransaction);

        for (InventoryButton value : BUTTONS.values()) {

            int inventorySlot = value.getInventorySlot();
            if (inventorySlot == -1) continue;

            ItemStack valueItem = new ItemBuilder(value.getItemStack(player.getName()))
                    .setLore(value.getLore()
                            .stream()
                            .map(line -> line
                                    .replace("$money", NumberUtils.format(account.getBalance()))
                                    .replace("$transactions", account.getTransactionsQuantity() + " " + transactionName)
                                    .replace("$movimentedMoney", NumberUtils.format(account.getMovimentedBalance()))
                                    .replace("$value", purse)
                                    .replace("$status", isHigh)
                                    .replace("$time", nextUpdate)
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

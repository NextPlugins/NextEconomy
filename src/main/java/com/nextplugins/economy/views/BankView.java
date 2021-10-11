package com.nextplugins.economy.views;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import com.nextplugins.economy.util.TimeUtils;
import com.nextplugins.economy.views.button.model.ButtonType;
import com.nextplugins.economy.views.button.registry.InventoryButtonRegistry;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class BankView extends SimpleInventory {

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
    protected void configureInventory(Viewer viewer, @NotNull InventoryEditor editor) {
        val player = viewer.getPlayer();
        val account = accountStorage.findAccount(player);
        val instance = PurseAPI.getInstance();
        val receiveType = ColorUtil.colored(account.isReceiveCoins() ? MessageValue.get(MessageValue::on) : MessageValue.get(MessageValue::off));
        val discordName = ColorUtil.colored(
                account.getDiscordName() == null
                ? "&cNão vinculado"
                : account.getDiscordName()
        );

        val purse = instance != null ? instance.getPurseFormated() : "";
        val isHigh = instance != null ? instance.getHighMessage() : "";

        val nextUpdate = instance != null
                ? TimeUtils.format(instance.getNextUpdate() - System.currentTimeMillis())
                : "";

        val transactionName = account.getTransactionsQuantity() == 1
                ? MessageValue.get(MessageValue::singularTransaction)
                : MessageValue.get(MessageValue::pluralTransaction);

        val transactionsMessage = account.getTransactionsQuantity() + " " + transactionName;

        for (val value : BUTTONS.values()) {
            val inventorySlot = value.getInventorySlot();
            if (inventorySlot == -1 || (value.getButtonType() == ButtonType.PURSE && instance == null)) continue;

            val valueItem = new ItemBuilder(value.getItemStack(player.getName()))
                    .setLore(value.getLore()
                            .stream()
                            .map(line -> line
                                    .replace("$money", account.getBalanceFormated())
                                    .replace("$transactions", transactionsMessage)
                                    .replace("$movimentedMoney", NumberUtils.format(account.getMovimentedBalance()))
                                    .replace("$toggleMessage", receiveType)
                                    .replace("$discord", discordName)
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

    @Override
    protected void update(@NotNull Viewer viewer, @NotNull InventoryEditor editor) {
        configureInventory(viewer, editor);
    }
}

package com.nextplugins.economy.views.button.model;

import com.henryfabio.minecraft.inventoryapi.event.impl.CustomInventoryClickEvent;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.views.registry.InventoryRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.Consumer;

@RequiredArgsConstructor
public enum ButtonType {

    PURSE(callback -> {
    }),

    HELP(callback -> {
        callback.getPlayer().closeInventory();
        callback.getPlayer().performCommand("money help");
    }),

    TOGGLE(callback -> {
        callback.getPlayer().performCommand("money toggle");
        callback.updateInventory();
    }),

    YOUR_MONEY(callback -> {
        val historicBankView = InventoryRegistry.getInstance().getHistoricBankView();
        historicBankView.openInventory(callback.getPlayer());
    }),

    SEND_MONEY(callback -> {
        val player = callback.getPlayer();
        player.closeInventory();

        val interaction = NextEconomy.getInstance().getInteractionRegistry().getPayInteractionManager();

        for (String message : MessageValue.get(MessageValue::interactionInputPlayer)) {
            player.sendMessage(message);
        }

        interaction.sendRequisition(player, false);
    }),

    VIEW_MONEY(callback -> {
        val player = callback.getPlayer();
        player.closeInventory();

        val interactionRegistry = NextEconomy.getInstance().getInteractionRegistry();
        if (interactionRegistry.getPayInteractionManager().isUsing(player)) player.chat("cancelar");

        val interaction = interactionRegistry.getLookupInteractionManager();

        for (String message : MessageValue.get(MessageValue::interactionInputPlayer)) {
            player.sendMessage(message);
        }

        interaction.sendRequisition(player);
    }),

    TOP_MONEY(callback -> {
        callback.getPlayer().closeInventory();
        callback.getPlayer().performCommand("money top");
    });

    @Getter
    private final Consumer<CustomInventoryClickEvent> action;

}

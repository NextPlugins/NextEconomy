package com.nextplugins.economy.listener.events.check;

import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.NumberUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class CheckInteractListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler
    public void onCheckInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) return;

        final NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("value")) return;

        final double checkValue = nbtItem.getDouble("value");

        final Account account = accountStorage.findOnlineAccount(player);

        double totalValue = 0;
        int totalChecks = 0;

        for (int i = 0; i < item.getAmount(); i++) {
            account.createTransaction(
                    null,
                    checkValue,
                    TransactionType.DEPOSIT
            );

            totalValue = totalValue + checkValue;

            player.setItemInHand(null);
            totalChecks++;
        }

        player.sendMessage(
                MessageValue.get(MessageValue::checkUsed)
                        .replace("$checkAmount", NumberUtils.format(totalChecks))
                        .replace("$checkValue", NumberUtils.format(checkValue))
                        .replace("$checkTotalValue", NumberUtils.format(totalValue))
        );
    }

}

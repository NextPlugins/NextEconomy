package com.nextplugins.economy.listener.events.check;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.NumberUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public final class CheckInteractListener implements Listener {

    private final AccountStorage accountStorage;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCheckInteract(PlayerInteractEvent event) {

        val player = event.getPlayer();

        val item = player.getItemInHand();
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || item.getType() == Material.AIR) return;

        val checkField = "value";

        val nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey(checkField)) return;

        event.setCancelled(true);
        player.setItemInHand(null);

        var value = nbtItem.getDouble(checkField) * item.getAmount();
        if (player.isSneaking()) {

            val contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {

                val content = contents[i];
                if (content == null || content.getType() == Material.AIR) continue;

                val contentNbt = new NBTItem(content);
                if (!contentNbt.hasKey(checkField)) continue;

                value += contentNbt.getDouble(checkField) * content.getAmount();
                contents[i] = null;

            }

            player.getInventory().setContents(contents);

        }

        val account = accountStorage.findAccount(player);

        account.createTransaction(
                player,
                "Cheque",
                value,
                TransactionType.DEPOSIT
        );

        player.sendMessage(
                MessageValue.get(MessageValue::checkUsed)
                        .replace("$checkAmount", NumberUtils.format(item.getAmount()))
                        .replace("$checkTotalValue", NumberUtils.format(value))
        );

    }

}

package com.nextplugins.economy.listener.events.transaction;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.util.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class TransactionRequestListener implements Listener {

    protected final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRequest(TransactionRequestEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer target = event.getTarget();
        double amount = event.getAmount();

        if (target.equals(player)) {
            player.sendMessage(MessageValue.get(MessageValue::isYourself));
            return;
        }

        Account account = accountStorage.findOnlineAccount(player);
        Account targetAccount = accountStorage.findOfflineAccount(target.getName());
        if (targetAccount == null) {

            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;

        }

        if (account.hasAmount(amount)) {
            targetAccount.depositAmount(amount);
            account.withdrawAmount(amount);

            player.sendMessage(
                    MessageValue.get(MessageValue::paid).replace("$player", target.getName())
                            .replace("$amount", NumberUtils.format(amount))
            );

            if (target.isOnline()) {
                target.getPlayer().sendMessage(
                        MessageValue.get(MessageValue::received).replace("$player", player.getName())
                                .replace("$amount", NumberUtils.format(amount))
                );
            }
        } else {
            player.sendMessage(MessageValue.get(MessageValue::insufficientAmount));
        }

    }

}

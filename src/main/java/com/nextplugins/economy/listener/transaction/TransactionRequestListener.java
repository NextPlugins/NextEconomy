package com.nextplugins.economy.listener.transaction;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.util.NumberFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TransactionRequestListener implements Listener {

    protected final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler
    public void onRequest(TransactionRequestEvent event) {
        Player player = event.getPlayer();
        Player target = event.getTarget();
        double amount = event.getAmount();

        if (target.equals(player)) {
            player.sendMessage(MessageValue.get(MessageValue::isYourself));
            return;
        }

        Account account = accountStorage.getAccount(player.getUniqueId());
        Account targetAccount = accountStorage.getAccount(target.getUniqueId());

        if (account.hasAmount(amount)) {
            targetAccount.depositAmount(amount);
            account.withdrawAmount(amount);

            player.sendMessage(
                    MessageValue.get(MessageValue::paid).replace("$player", target.getName())
                            .replace("$amount", NumberFormat.format(amount))
            );

            if (target.isOnline()) {
                target.getPlayer().sendMessage(
                        MessageValue.get(MessageValue::received).replace("$player", player.getName())
                                .replace("$amount", NumberFormat.format(amount))
                );
            }
        } else {
            player.sendMessage(MessageValue.get(MessageValue::insufficientAmount));
        }

    }

}

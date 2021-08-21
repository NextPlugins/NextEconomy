package com.nextplugins.economy.listener.events.transaction;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class TransactionRequestListener implements Listener {

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRequest(TransactionRequestEvent event) {

        if (event.isCancelled()) return;

        val player = event.getPlayer();
        val target = event.getTarget();
        val targetAccount = event.getAccount();
        val amount = event.getAmount();

        if (target.equals(player)) {
            player.sendMessage(MessageValue.get(MessageValue::isYourself));
            return;
        }

        val account = accountStorage.findAccount(player);
        if (NumberUtils.isInvalid(amount)) {

            player.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;

        }

        val minValue = FeatureValue.get(FeatureValue::minTransactionValue);
        if (amount < minValue) {

            player.sendMessage(MessageValue.get(MessageValue::minValueNecessary)
                    .replace("$amount", NumberUtils.format(minValue))
            );
            return;

        }

        if (account.hasAmount(amount)) {

            targetAccount.createTransaction(
                    target.isOnline() ? target.getPlayer() : null,
                    player.getName(),
                    amount,
                    0,
                    TransactionType.DEPOSIT
            );

            account.createTransaction(
                    player,
                    target.getName(),
                    amount,
                    0,
                    TransactionType.WITHDRAW
            );

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

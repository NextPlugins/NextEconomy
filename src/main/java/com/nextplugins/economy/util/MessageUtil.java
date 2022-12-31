package com.nextplugins.economy.util;

import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.StockExchangeValue;
import com.nextplugins.economy.model.account.transaction.Transaction;
import com.nextplugins.economy.model.account.transaction.TransactionType;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public class MessageUtil {

    public static void sendPurseAffectMessage(@NotNull Transaction transaction) {
        val amount = transaction.amount();
        val amountWithoutPurse = transaction.amountWithoutPurse();

        String message;
        if (transaction.transactionType() == TransactionType.WITHDRAW) {
            if (amount > amountWithoutPurse) message = MessageValue.get(MessageValue::purseSpendMore);
            else message = MessageValue.get(MessageValue::purseSpendLess);
        } else {
            if (amount > amountWithoutPurse) message = MessageValue.get(MessageValue::purseReceiveMore);
            else message = MessageValue.get(MessageValue::purseReceiveLess);
        }

        val value = amount > amountWithoutPurse ? amount - amountWithoutPurse : amountWithoutPurse - amount;
        val purseMessage = message.replace("$value", NumberUtils.format(value));

        val isMessageMethod = StockExchangeValue.get(StockExchangeValue::messageMethod).equalsIgnoreCase("message");
        if (isMessageMethod) transaction.player().sendMessage(purseMessage);
        else ActionBarUtils.sendActionBar(transaction.player(), purseMessage);
    }

}

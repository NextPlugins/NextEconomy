package com.nextplugins.economy.interactions.sendmoney.model;

import lombok.Data;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class SendMoneyInteraction {

    private SendMoneyInteractionStep step;

    private OfflinePlayer target;
    private double amount;

    public SendMoneyInteraction(SendMoneyInteractionStep step) {
        this.step = step;
    }

    public static SendMoneyInteraction create() {
        return new SendMoneyInteraction(SendMoneyInteractionStep.PLAYER_NAME);
    }

}

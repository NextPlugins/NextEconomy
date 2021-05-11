package com.nextplugins.economy.api.model.interactions;

import lombok.Data;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class PayInteraction {

    private PayInteractionStep step;

    private OfflinePlayer target;
    private double amount;

    public PayInteraction(PayInteractionStep step) {
        this.step = step;
    }

    public static PayInteraction create() {
        return new PayInteraction(PayInteractionStep.PLAYER_NAME);
    }

}

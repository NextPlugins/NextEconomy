package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Setter
public final class MoneyChangeEvent extends EconomyEvent {

    private final Player player;
    private final Account account;

    private final double currentAmount;
    private final String amountFormated;

    public MoneyChangeEvent(Player player, Account account, double currentAmount, String amountFormated) {
        super(false);
        this.player = player;
        this.account = account;
        this.currentAmount = currentAmount;
        this.amountFormated = amountFormated;
    }
}

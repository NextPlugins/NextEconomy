package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class MoneyChangeEvent extends EconomyEvent {

    private final Player player;
    private final Account account;

    private final double currentAmount;
    private final String amountFormated;

}

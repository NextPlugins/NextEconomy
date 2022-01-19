package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public final class MoneyGiveEvent extends EconomyEvent implements Cancellable {

    private final CommandSender sender;
    private final OfflinePlayer target;
    private final double amountBeforePurse; // amount not affected by purse multiplier
    private final double amount;
    private boolean cancelled;

    @Deprecated
    public MoneyGiveEvent(CommandSender sender, OfflinePlayer target, double amount) {
        super(false);
        this.sender = sender;
        this.target = target;
        this.amount = amount;
        this.amountBeforePurse = amount;
    }

    public MoneyGiveEvent(CommandSender sender, OfflinePlayer target, double amount, double amountBeforePurse) {
        super(false);
        this.sender = sender;
        this.target = target;
        this.amount = amount;
        this.amountBeforePurse = amountBeforePurse;
    }
}

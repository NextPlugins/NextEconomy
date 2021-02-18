package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Data
@EqualsAndHashCode(callSuper = true)
public final class MoneySetEvent extends EconomyEvent implements Cancellable {

    private final Player player;
    private final Player target;
    private final double amount;
    private boolean cancelled;

}

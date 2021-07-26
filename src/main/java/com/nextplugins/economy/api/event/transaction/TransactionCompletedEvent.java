package com.nextplugins.economy.api.event.transaction;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public final class TransactionCompletedEvent extends EconomyEvent {

    private final Player player;
    private final OfflinePlayer target;
    private final double amount;

    public TransactionCompletedEvent(Player player, OfflinePlayer target, double amount) {
        super(false);
        this.player = player;
        this.target = target;
        this.amount = amount;
    }
}

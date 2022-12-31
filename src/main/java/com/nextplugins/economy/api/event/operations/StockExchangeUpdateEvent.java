package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Setter
public final class StockExchangeUpdateEvent extends EconomyEvent implements Cancellable {

    private final int newValue;
    private final int lastValue;
    private boolean cancelled;

    public StockExchangeUpdateEvent(int newValue, int lastValue) {
        super(false);
        this.newValue = newValue;
        this.lastValue = lastValue;
    }

}


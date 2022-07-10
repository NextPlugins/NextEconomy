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
public final class AsyncPurseUpdateEvent extends EconomyEvent implements Cancellable {

    private final int newValue;
    private final int lastValue;
    private final long updateMillis;
    private boolean cancelled;

    public AsyncPurseUpdateEvent(int newValue, int lastValue, long updateMillis) {
        super(true);
        this.newValue = newValue;
        this.lastValue = lastValue;
        this.updateMillis = updateMillis;
    }
}


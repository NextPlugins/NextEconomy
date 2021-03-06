package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@EqualsAndHashCode(callSuper = true)
public final class AsyncPurseUpdateEvent extends EconomyEvent implements Cancellable {

    private final int newValue;
    private final int lastValue;
    private final long updateMillis;
    private boolean cancelled;

    private final boolean async = true;

}


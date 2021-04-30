package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@EqualsAndHashCode(callSuper = true)
public final class AsyncPurseUpdateEvent extends EconomyEvent implements Cancellable {

    private final int newValue;
    private final int lastValue;
    private final Instant instant;
    private final long nextUpdate;
    private boolean cancelled;

    private final boolean async = true;

}


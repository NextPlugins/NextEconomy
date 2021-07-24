package com.nextplugins.economy.api.event.operations;

import com.nextplugins.economy.api.event.EconomyEvent;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public final class MoneyTopPlayerChangedEvent extends EconomyEvent implements Cancellable {

    private final SimpleAccount lastMoneyTop;
    private final SimpleAccount moneyTop;

    private final Instant updateInstant;
    private final boolean async;
    private boolean cancelled;

}

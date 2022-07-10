package com.nextplugins.economy.api.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class EconomyEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    public EconomyEvent(boolean isAsync) {
        super(isAsync);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}

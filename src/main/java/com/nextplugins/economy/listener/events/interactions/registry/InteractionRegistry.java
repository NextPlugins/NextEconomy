package com.nextplugins.economy.listener.events.interactions.registry;

import com.nextplugins.economy.listener.events.interactions.PayInteractionManager;
import com.nextplugins.economy.listener.events.interactions.LookupInteractionManager;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
public final class InteractionRegistry {

    private PayInteractionManager payInteractionManager;
    private LookupInteractionManager lookupInteractionManager;

    public void init() {

        this.payInteractionManager = new PayInteractionManager().init();
        this.lookupInteractionManager = new LookupInteractionManager().init();

    }

}

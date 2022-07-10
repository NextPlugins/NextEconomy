package com.nextplugins.economy.model.interactions.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.model.interactions.manager.LookupInteractionManager;
import com.nextplugins.economy.model.interactions.manager.PayInteractionManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
public final class InteractionRegistry {

    // mega pog
    private final List<String> operation = new ArrayList<>();

    private PayInteractionManager payInteractionManager;
    private LookupInteractionManager lookupInteractionManager;

    public void init() {
        this.payInteractionManager = new PayInteractionManager().init();
        this.lookupInteractionManager = new LookupInteractionManager().init();

        NextEconomy.getInstance().getLogger().info("Interações via chat registradas com sucesso.");
    }

}

package com.nextplugins.economy.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.interactions.sendmoney.SendMoneyInteractionManager;
import com.nextplugins.economy.interactions.viewplayer.ViewPlayerInteractionManager;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
public final class InteractionRegistry {

    @Getter private static final InteractionRegistry instance = new InteractionRegistry();

    private SendMoneyInteractionManager sendMoneyInteractionManager;
    private ViewPlayerInteractionManager viewPlayerInteractionManager;

    public void init(NextEconomy plugin) {

        this.sendMoneyInteractionManager = new SendMoneyInteractionManager().init();
        this.viewPlayerInteractionManager = new ViewPlayerInteractionManager();

    }

}

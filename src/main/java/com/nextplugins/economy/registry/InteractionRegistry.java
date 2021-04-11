package com.nextplugins.economy.registry;

import com.nextplugins.economy.interactions.sendmoney.SendMoneyInteractionManager;
import com.nextplugins.economy.interactions.viewplayer.ViewPlayerInteractionManager;
import lombok.Getter;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@Getter
public final class InteractionRegistry {

    private SendMoneyInteractionManager sendMoneyInteractionManager;
    private ViewPlayerInteractionManager viewPlayerInteractionManager;

    public void init() {

        this.sendMoneyInteractionManager = new SendMoneyInteractionManager().init();
        this.viewPlayerInteractionManager = new ViewPlayerInteractionManager().init();

    }

}

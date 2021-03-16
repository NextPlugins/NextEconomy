package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.api.event.operations.MoneyTopPlayerUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MoneyTopUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTopUpdate(MoneyTopPlayerUpdateEvent event) {

        if (event.isCancelled()) return;


    }

}

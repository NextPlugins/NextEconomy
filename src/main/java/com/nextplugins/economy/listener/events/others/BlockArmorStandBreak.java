package com.nextplugins.economy.listener.events.others;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class BlockArmorStandBreak implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEDeath(EntityDamageEvent event) {
        event.setCancelled(event.getEntityType() == EntityType.ARMOR_STAND && event.getEntity().hasMetadata("nexteconomy"));
    }

}
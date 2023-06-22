package com.nextplugins.economy.listener.events.others;

import com.nextplugins.economy.ranking.types.ArmorStandRunnable;
import lombok.val;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class RankingEntityListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND || !event.getEntity().hasMetadata("nexteconomy")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND || !event.getEntity().hasMetadata("nexteconomy")) return;
        ArmorStandRunnable.STANDS.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        val entity = event.getRightClicked();
        if (entity.getType() != EntityType.ARMOR_STAND || !entity.hasMetadata("nexteconomy")) return;

        event.getPlayer().performCommand("money top");
        event.setCancelled(true);
    }

}
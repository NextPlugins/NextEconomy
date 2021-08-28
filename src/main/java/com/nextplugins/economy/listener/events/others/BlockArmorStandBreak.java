package com.nextplugins.economy.listener.events.others;

import com.nextplugins.economy.ranking.types.ArmorStandRunnable;
import com.nextplugins.economy.util.TypeUtil;
import lombok.val;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class BlockArmorStandBreak implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND || !event.getEntity().hasMetadata("nexteconomy")) return;

        if (event.getDamager() instanceof Player) {

            val damager = (Player) event.getDamager();
            val itemInHand = damager.getItemInHand();
            if (itemInHand != null && itemInHand.getType() == TypeUtil.swapLegacy("GOLDEN_SWORD", "GOLD_SWORD")) return;

        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND || !event.getEntity().hasMetadata("nexteconomy")) return;
        ArmorStandRunnable.STANDS.remove((ArmorStand) event.getEntity());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        val entity = event.getRightClicked();
        if (entity.getType() != EntityType.ARMOR_STAND || !entity.hasMetadata("nexteconomy")) return;

        event.getPlayer().performCommand("money top");
        event.setCancelled(true);
    }

}
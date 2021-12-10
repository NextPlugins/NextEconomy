package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.NextEconomy;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateCheckerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("nexteconomy.admin")) return;

        val updateChecker = NextEconomy.getInstance().getUpdateChecker();
        if (!updateChecker.canUpdate()) return;

        val newVersionComponent = new TextComponent(String.format(
                " Uma nova versão do NextEconomy está disponível (%s » %s)",
                updateChecker.getCurrentVersion(),
                updateChecker.getMoreRecentVersion())
        );

        newVersionComponent.setColor(ChatColor.GREEN);
        newVersionComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, updateChecker.getDownloadLink()));

        val downloadComponent = new TextComponent(" Clique aqui para ir até o local de download.");
        downloadComponent.setColor(ChatColor.GRAY);
        downloadComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, updateChecker.getDownloadLink()));

        val player = event.getPlayer();
        val spigotPlayer = player.spigot();

        player.sendMessage("");
        spigotPlayer.sendMessage(newVersionComponent);
        spigotPlayer.sendMessage(downloadComponent);
        player.sendMessage("");
    }

}

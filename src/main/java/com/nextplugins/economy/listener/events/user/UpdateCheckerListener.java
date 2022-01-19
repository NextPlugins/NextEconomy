package com.nextplugins.economy.listener.events.user;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateCheckerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("nexteconomy.admin")) return;

        val updateChecker = NextEconomy.getInstance().getUpdateChecker();
        if (!updateChecker.canUpdate()) return;

        val lastRelease = updateChecker.getLastRelease();
        if (lastRelease == null) return;

        val newVersionComponent = new TextComponent(String.format(
                " Uma nova versão do NextEconomy está disponível (%s » %s)",
                updateChecker.getCurrentVersion(),
                lastRelease.getVersion()
        ));

        val downloadComponent = new TextComponent(" Clique aqui para ir até o local de download.");
        val channelComponent = new TextComponent(TextComponent.fromLegacyText(ColorUtil.colored(
                " &7Canal de atualização: " + getChannel(lastRelease.isPreRelease() || lastRelease.isDraft()))
        ));

        val hoverText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ColorUtil.colored("&7Este link irá levar até o github do plugin")));
        val clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, lastRelease.getDownloadURL());

        newVersionComponent.setColor(ChatColor.GREEN);
        downloadComponent.setColor(ChatColor.GRAY);

        newVersionComponent.setHoverEvent(hoverText);
        downloadComponent.setHoverEvent(hoverText);
        channelComponent.setHoverEvent(hoverText);

        newVersionComponent.setClickEvent(clickEvent);
        downloadComponent.setClickEvent(clickEvent);
        channelComponent.setClickEvent(clickEvent);

        val player = event.getPlayer();
        val spigotPlayer = player.spigot();

        // avoid chat cleaners when join
        Bukkit.getScheduler().runTaskLater(NextEconomy.getInstance(), () -> {
            player.sendMessage("");
            spigotPlayer.sendMessage(newVersionComponent);
            spigotPlayer.sendMessage(downloadComponent);
            spigotPlayer.sendMessage(channelComponent);
            player.sendMessage("");
        }, 5 * 20L);
    }

    private String getChannel(boolean betaChannel) {
        return betaChannel ? "&6Beta" : "&aEstável";
    }

}

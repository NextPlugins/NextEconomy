package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.api.event.operations.MoneyTopPlayerChangedEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MoneyTopUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTopUpdate(MoneyTopPlayerChangedEvent event) {

        if (event.isCancelled() || !MessageValue.get(MessageValue::enableMoneyTopMessage)) return;

        String username = event.getMoneyTop().getUsername();

        String title = MessageValue.get(MessageValue::moneyTopTitle)
                .replace("$player", username);

        List<String> message = new ArrayList<>();
        for (String line : MessageValue.get(MessageValue::moneyTopMessage)) {
            message.add(line.replace("$player", username));
        }

        Object[] titlePackets = TitleUtils.buildTitlePackets(title, 20, 20, 20);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            TitleUtils.sendTitlePacket(onlinePlayer, titlePackets);

            for (String s : message) {
                onlinePlayer.sendMessage(s);
            }

        }

    }

}

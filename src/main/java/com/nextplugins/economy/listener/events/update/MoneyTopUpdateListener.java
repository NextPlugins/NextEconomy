package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.api.event.operations.MoneyTopPlayerUpdateEvent;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class MoneyTopUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTopUpdate(MoneyTopPlayerUpdateEvent event) {

        if (event.isCancelled() || !MessageValue.get(MessageValue::enableMoneyTopMessage)) return;

        String userName = event.getMoneyTop().getUserName();

        String title = MessageValue.get(MessageValue::moneyTopTitle)
                .replace("$player", userName);

        List<String> message = MessageValue.get(MessageValue::moneyTopMessage).stream()
                .map(line -> line.replace("$player", userName))
                .collect(Collectors.toList());

        Object[] titlePackets = TitleUtils.buildTitlePackets(title, 20, 20, 20);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            TitleUtils.sendTitlePacket(onlinePlayer, titlePackets);
            message.forEach(onlinePlayer::sendMessage);

        }

    }

}

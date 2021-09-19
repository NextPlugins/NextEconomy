package com.nextplugins.economy.listener.events.update;

import com.nextplugins.economy.api.event.operations.AsyncMoneyTopPlayerChangedEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.util.TitleUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TopUpdateListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTopUpdate(AsyncMoneyTopPlayerChangedEvent event) {

        if (event.isCancelled()) return;

        val username = event.getMoneyTop().getUsername();

        if (MessageValue.get(MessageValue::enableMoneyTopMessage)) {
            val title = MessageValue.get(MessageValue::moneyTopTitle)
                    .replace("$player", username);

            val message = new ArrayList<String>();
            for (val line : MessageValue.get(MessageValue::moneyTopMessage)) {
                message.add(line.replace("$player", username));
            }

            val titlePackets = TitleUtils.buildTitlePackets(title, 20, 20, 20);
            for (val onlinePlayer : Bukkit.getOnlinePlayers()) {

                TitleUtils.sendTitlePacket(onlinePlayer, titlePackets);

                for (val s : message) {
                    onlinePlayer.sendMessage(s);
                }

            }
        }

        for (String s : RankingValue.get(RankingValue::tycoonCommands)) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    s.replace("$currentTycoon", username).replace("$lastTycoon", event.getLastMoneyTop().getUsername())
            );
        }

    }

}

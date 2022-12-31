package com.nextplugins.economy.listener.events.update;

import com.github.juliarn.npc.modifier.LabyModModifier;
import com.nextplugins.economy.NextEconomy;
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
import java.util.Random;
import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TopUpdateListener implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTopUpdate(AsyncMoneyTopPlayerChangedEvent event) {
        if (event.isCancelled()) return;

        if (AnimationValue.get(AnimationValue::enable)) {
            val emotes = AnimationValue.get(AnimationValue::magnataEmotes);
            val emote = emotes.get(RANDOM.nextInt(emotes.size()));
            try {
                val splittedValue = emote.split(":");

                val labyModAction = LabyModModifier.LabyModAction.valueOf(splittedValue[0].toUpperCase());
                val actionName = labyModAction.name().toLowerCase();

                val actionId = Integer.parseInt(splittedValue[1]);

                Bukkit.getScheduler().runTask(
                      NextEconomy.getInstance(),
                      () -> Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "nexteconomy playanimation 1 " + actionName + " " + actionId
                      )
                );
            } catch (Throwable throwable) {
                NextEconomy.getInstance().getLogger().log(
                        Level.SEVERE,
                        "Magnata update animation value pattern malformed. (should be: \"sticker/emote:ID\")",
                        throwable
                );
            }

            try {
                val splittedValue = AnimationValue.get(AnimationValue::rageDance).split(":");

                val labyModAction = LabyModModifier.LabyModAction.valueOf(splittedValue[0].toUpperCase());
                val actionName = labyModAction.name().toLowerCase();

                val actionId = Integer.parseInt(splittedValue[1]);

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nexteconomy playanimation 2 " + actionName + " " + actionId);
            } catch (Throwable throwable) {
                NextEconomy.getInstance().getLogger().log(
                        Level.SEVERE,
                        "Rage animation value pattern malformed. (should be: \"sticker/emote:ID\")",
                        throwable
                );
            }
        }

        val username = event.getMoneyTop().getUsername();
        if (MessageValue.get(MessageValue::enableMoneyTopMessage)) {
            val title = MessageValue.get(MessageValue::moneyTopTitle)
                    .replace("$player", username);

            val message = new ArrayList<String>();
            for (val line : MessageValue.get(MessageValue::moneyTopMessage)) {
                message.add(line.replace("$player", username).replace("$coins", event.getMoneyTop().getBalanceFormatted()));
            }

            if (title.contains("<nl>")) {
                val titlePackets = TitleUtils.buildTitlePackets(title, 20, 20, 20);
                for (val onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (titlePackets == null) {
                        TitleUtils.sendTitle(onlinePlayer, title, 20, 20, 20);
                    } else {
                        TitleUtils.sendPacketsToPlayer(onlinePlayer, titlePackets);
                    }

                    for (val s : message) {
                        onlinePlayer.sendMessage(s);
                    }
                }
            }
        }

        for (String s : RankingValue.get(RankingValue::tycoonCommands)) {
            Bukkit.getScheduler().runTask(NextEconomy.getInstance(), () -> Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    s.replace("$currentTycoon", username).replace("$lastTycoon", event.getLastMoneyTop().getUsername())
            ));
        }
    }

}

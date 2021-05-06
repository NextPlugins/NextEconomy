package com.nextplugins.economy.listener.events.operation;

import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.event.operations.AsyncPurseUpdateEvent;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.SoundUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class AsyncPurseUpdateListener implements Listener {

    private static final PurseAPI purseAPI = PurseAPI.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPurseUpdate(AsyncPurseUpdateEvent event) {

        if (purseAPI == null) return;

        purseAPI.setPurse(event.getNewValue());
        purseAPI.setNextUpdate(event.getNextUpdate());

        boolean equals = event.getNewValue() == event.getLastValue();
        boolean difference = event.getNewValue() > event.getLastValue();

        String operationIcon = MessageValue.get(MessageValue::equalsIcon);
        String operationMessage = MessageValue.get(MessageValue::equalsMessage);

        if (!equals) {

            operationIcon = difference
                    ? MessageValue.get(MessageValue::valuedIcon)
                    : MessageValue.get(MessageValue::devaluedIcon);

            operationMessage = difference
                    ? MessageValue.get(MessageValue::valuedMessage)
                    : MessageValue.get(MessageValue::devaluedMessage);

        }

        String finalOperationIcon = operationIcon;
        String finalOperationMessage = operationMessage;

        val message = MessageValue.get(MessageValue::purseUpdatedMessage).stream()
                .map(line -> line.replace("$lastvalue", event.getLastValue() + "%")
                        .replace("$operationIcon", finalOperationIcon)
                        .replace("$newvalue", event.getNewValue() + "%")
                        .replace("$operationMessage", finalOperationMessage))
                .map(ColorUtil::colored)
                .collect(Collectors.toList());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            SoundUtils.sendSound(onlinePlayer, Sound.valueOf(MessageValue.get(MessageValue::purseUpdatedSound)));
            for (String line : message) onlinePlayer.sendMessage(line);
        }

    }

}

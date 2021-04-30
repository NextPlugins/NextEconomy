package com.nextplugins.economy.listener.events.interactions;

import com.google.common.collect.Maps;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.EventAwaiter;
import com.nextplugins.economy.util.NumberUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class PayInteractionManager {

    private static final String COMMAND = "money enviar %s %s";

    @Getter private final Map<String, PayInteraction> players = Maps.newHashMap();

    private Consumer<AsyncPlayerChatEvent> consumer;

    public void sendRequisition(Player player, boolean inUse) {

        if (!inUse) players.put(player.getName(), PayInteraction.create());

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextEconomy.getInstance())
                .expiringAfter(1, TimeUnit.MINUTES)
                .withTimeOutAction(() -> {
                    player.sendMessage(MessageValue.get(MessageValue::noTime));
                    players.remove(player.getName());
                })
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(true);

    }

    public void sendConfirmation(Player player) {

        EventAwaiter.newAwaiter(AsyncPlayerChatEvent.class, NextEconomy.getInstance())
                .expiringAfter(10, TimeUnit.SECONDS)
                .withTimeOutAction(() -> {

                    PayInteraction payInteraction = players.get(player.getName());

                    Bukkit.getScheduler().runTask(
                            NextEconomy.getInstance(),
                            () -> player.performCommand(String.format(COMMAND,
                            payInteraction.getTarget().getName(),
                            payInteraction.getAmount())
                    ));

                    players.remove(player.getName());

                })
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(false);

    }

    public boolean isUsing(Player player) {
        return players.containsKey(player.getName());
    }

    public PayInteractionManager init() {

        consumer = event -> {

            event.setCancelled(true);

            val player = event.getPlayer();
            val message = event.getMessage();

            if (message.equalsIgnoreCase("cancelar")) {

                players.remove(player.getName());
                player.sendMessage(MessageValue.get(MessageValue::interactionCancelled));
                return;

            }

            PayInteraction payInteraction = players.get(player.getName());
            PayInteractionStep step = payInteraction.getStep();

            switch (step) {

                case PLAYER_NAME: {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(message);
                    if (offlinePlayer == null) {

                        player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
                        return;

                    }

                    payInteraction.setTarget(offlinePlayer);
                    payInteraction.setStep(PayInteractionStep.QUANTITY);

                    MessageValue.get(MessageValue::interactionInputMoney).forEach(player::sendMessage);

                    sendRequisition(player, true);
                    return;

                }

                default: {

                    double parse = NumberUtils.parse(message);
                    if (parse < 1) {

                        player.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                        return;

                    }

                    payInteraction.setAmount(parse);
                    payInteraction.setStep(PayInteractionStep.CONFIRM);

                    for (String line : MessageValue.get(MessageValue::interactionConfirm)) {
                        String replace = line
                                .replace("@money", NumberUtils.format(payInteraction.getAmount()))
                                .replace("@player", payInteraction.getTarget().getName());

                        player.sendMessage(replace);
                    }

                    sendConfirmation(player);
                    return;

                }

                case CONFIRM: {

                    player.sendMessage(MessageValue.get(MessageValue::interactionInvalid));
                    sendConfirmation(player);

                }

            }

        };

        return this;

    }

}

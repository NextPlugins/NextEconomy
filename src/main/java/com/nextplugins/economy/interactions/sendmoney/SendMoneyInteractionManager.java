package com.nextplugins.economy.interactions.sendmoney;

import com.google.common.collect.Maps;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.interactions.sendmoney.model.SendMoneyInteraction;
import com.nextplugins.economy.interactions.sendmoney.model.SendMoneyInteractionStep;
import com.nextplugins.economy.util.EventAwaiter;
import com.nextplugins.economy.util.NumberUtils;
import lombok.Getter;
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
public class SendMoneyInteractionManager {

    private static final String COMMAND = "money enviar %s %s";

    @Getter private final Map<String, SendMoneyInteraction> players = Maps.newHashMap();

    private Consumer<AsyncPlayerChatEvent> consumer;

    public void sendRequisition(Player player) {

        if (!players.containsKey(player.getName())) players.put(player.getName(), SendMoneyInteraction.create());

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

                    SendMoneyInteraction sendMoneyInteraction = players.get(player.getName());

                    player.performCommand(String.format(COMMAND,
                            sendMoneyInteraction.getTarget().getName(),
                            sendMoneyInteraction.getAmount())
                    );

                    players.remove(player.getName());

                })
                .filter(event -> event.getPlayer().getName().equals(player.getName()))
                .thenAccept(consumer)
                .await(true);

    }

    public SendMoneyInteractionManager init() {

        consumer = event -> {

            event.setCancelled(true);

            Player player = event.getPlayer();
            String message = event.getMessage();
            if (message.equalsIgnoreCase("cancelar")) {

                players.remove(player.getName());
                player.sendMessage(MessageValue.get(MessageValue::interactionCancelled));
                return;

            }

            SendMoneyInteraction sendMoneyInteraction = players.get(player.getName());
            SendMoneyInteractionStep step = sendMoneyInteraction.getStep();

            switch (step) {

                case PLAYER_NAME: {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(message);
                    if (offlinePlayer == null) {

                        player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
                        return;

                    }

                    MessageValue.get(MessageValue::interactionInputMoney).forEach(player::sendMessage);

                    sendMoneyInteraction.setTarget(offlinePlayer);
                    sendMoneyInteraction.setStep(SendMoneyInteractionStep.QUANTITY);

                    sendRequisition(player);
                    return;

                }

                case QUANTITY: {

                    double parse = NumberUtils.parse(message);
                    if (parse < 1) {

                        player.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                        return;

                    }

                    MessageValue.get(MessageValue::interactionConfirm)
                            .stream()
                            .map(line -> line
                                    .replace("@money", NumberUtils.format(sendMoneyInteraction.getAmount()))
                                    .replace("@player", sendMoneyInteraction.getTarget().getName())
                            )
                            .forEach(player::sendMessage);

                    sendMoneyInteraction.setAmount(parse);
                    sendMoneyInteraction.setStep(SendMoneyInteractionStep.CONFIRM);

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

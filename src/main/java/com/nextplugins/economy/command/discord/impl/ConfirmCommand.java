package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.model.discord.manager.PayActionDiscordManager;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class ConfirmCommand implements Command {

    private final PayActionDiscordManager payActionDiscordManager;

    @Override
    public void execute(Message message, String[] args) {
        val userId = message.getAuthor().getIdLong();
        val payActionDiscord = payActionDiscordManager.getCache().getIfPresent(userId);
        if (payActionDiscord == null) {
            message.reply(":x: Você não solicitou uma transação, utilize `" + DiscordValue.get(DiscordValue::prefix) +"pagar`.").queue();
            return;
        }

        payActionDiscordManager.getCache().invalidate(userId);
        payActionDiscordManager.confirm(payActionDiscord);
    }
}

package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.util.ColorUtil;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.val;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class HelpCommand implements Command {

    @Override
    public void execute(Message message, String[] args) {
        val stringBuilder = new StringBuilder();
        for (val line : DiscordValue.get(DiscordValue::helpDescription)) {
            stringBuilder.append(line).append("\n");
        }

        val embedBuilder = new EmbedBuilder()
                .setTitle(DiscordValue.get(DiscordValue::helpTitle))
                .setDescription(stringBuilder.toString())
                .setFooter(
                        DiscordValue.get(DiscordValue::helpFooter),
                        DiscordValue.get(DiscordValue::helpFooterImage)
                )
                .setColor(ColorUtil.getColorByHex(DiscordValue.get(DiscordValue::helpColor)));

        if (DiscordValue.get(DiscordValue::helpDate)) {
            embedBuilder.setTimestamp(Instant.now());
        }

        message.reply(embedBuilder.build()).queue();
    }

}

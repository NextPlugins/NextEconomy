package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.val;

import java.time.Instant;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TopMoneyCommand implements Command {

    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    @Override
    public void execute(Message message, String[] args) {

        val embedBuilder = new EmbedBuilder()
                .setTitle(DiscordValue.get(DiscordValue::topTitle))
                .setFooter(
                        DiscordValue.get(DiscordValue::topFooter),
                        DiscordValue.get(DiscordValue::topFooterImage)
                )
                .setColor(ColorUtil.getColorByHex(DiscordValue.get(DiscordValue::topColor)));

        if (DiscordValue.get(DiscordValue::topDate)) {
            embedBuilder.setTimestamp(Instant.now());
        }

        val stringBuilder = new StringBuilder();
        if (rankingStorage.getRankByCoin().isEmpty()) {
            stringBuilder.append(":x: Nenhum jogador está no ranking!");
        }

        val line = DiscordValue.get(DiscordValue::topLine);

        int position = 1;
        for (val economyUser : rankingStorage.getRankByCoin()) {

            if (position == 1) stringBuilder.append(DiscordValue.get(DiscordValue::topEmoji));
            stringBuilder.append(line
                    .replace("$postion", String.valueOf(position))
                    .replace("$username", economyUser.getUsername())
                    .replace("$coins", economyUser.getBalanceFormated())
            ).append("\n");

            ++position;

        }

        embedBuilder.setDescription(DiscordValue.get(DiscordValue::topDescription) + stringBuilder);
        message.reply(embedBuilder.build()).queue();

    }

}

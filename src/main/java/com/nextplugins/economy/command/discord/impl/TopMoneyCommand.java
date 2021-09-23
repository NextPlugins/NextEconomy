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
        if (rankingStorage.updateRanking(false)) {
            message.reply(DiscordValue.get(DiscordValue::loadingEmoji) + " Atualizando o ranking, aguarde alguns segundos.").queue();
            return;
        }

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

        val bodyLines = NextEconomy.getInstance().getRankingChatBody().getDiscordBodyLines();
        embedBuilder.setDescription(DiscordValue.get(DiscordValue::topDescription) + "\n\n" + bodyLines);
        message.reply(embedBuilder.build()).queue();
    }

}

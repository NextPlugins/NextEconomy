package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class ViewMoneyCommand implements Command {

    private AccountStorage accountStorage;

    @Override
    public void execute(Message message, String[] args) {

        OfflinePlayer player = null;

        val mentionedMembers = message.getMentionedMembers();
        if (mentionedMembers.size() > 1) {

            val member = mentionedMembers.get(0);
            if (!member.getUser().isBot()) {

                val uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId());
                player = Bukkit.getOfflinePlayer(uuid);

            }

        } else if (args.length > 0) {

            val memberName = args[0];
            player = Bukkit.getOfflinePlayer(memberName);

        }

        if (player == null) {

            message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                    " Você precisa mencionar um usuário, ou inserir um nick válido."
            ).queue();
            return;

        }

        message.reply(DiscordValue.get(DiscordValue::loadingEmoji) +
                " Procurando os dados do jogador informado."
        ).queue(value -> value.delete().queueAfter(3, TimeUnit.SECONDS));

        val account = accountStorage.findAccount(player);
        if (account == null) {

            message.reply(DiscordValue.get(DiscordValue::errorEmoji) +
                    " O jogador não foi encontrado em nosso servidor."
            ).queue();
            return;

        }

        val embedBuilder = new EmbedBuilder()
                .setTitle("")
                .setTimestamp(Instant.now())
                .setColor(12);

        message.reply(embedBuilder.build()).queue();

    }
}

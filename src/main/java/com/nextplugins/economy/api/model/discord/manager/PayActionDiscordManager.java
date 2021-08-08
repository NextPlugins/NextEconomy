package com.nextplugins.economy.api.model.discord.manager;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.api.model.discord.PayActionDiscord;
import com.nextplugins.economy.configuration.MessageValue;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class PayActionDiscordManager extends ListenerAdapter {

    @Getter private final Map<Long, PayActionDiscord> actions = new HashMap<>();
    private final AccountStorage accountStorage;

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

        if (!actions.containsKey(event.getMessageIdLong())) return;

        val payActionDiscord = actions.get(event.getMessageIdLong());
        actions.remove(event.getMessageIdLong());

        val account = accountStorage.findAccount(payActionDiscord.player());
        if (account == null) return;

        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {

            if (event.getReactionEmote().getAsReactionCode().equalsIgnoreCase("✅")) confirm(payActionDiscord, message);
            else message.delete().queue();


        });

    }

    private void confirm(PayActionDiscord payActionDiscord, Message message) {

        val player = payActionDiscord.player();
        val target = payActionDiscord.target();

        val account = accountStorage.findAccount(player);
        val targetAccount = accountStorage.findAccount(target);
        val amount = payActionDiscord.value();

        if (account == null || targetAccount == null) {
            // 1.10^-11 chance
            message.reply("⁉️ 404: Ocorreu um erro ao obter as contas dos jogadores envolvidos.").queue();
            return;
        }

        if (!account.hasAmount(amount)) {
            message.reply(MessageValue.get(MessageValue::noCoinsDiscord)).queue();
            return;
        }

        account.createTransaction(
                payActionDiscord.player().isOnline() ? payActionDiscord.player().getPlayer() : null,
                target.getName(),
                amount,
                TransactionType.WITHDRAW
        );

        targetAccount.createTransaction(
                target.isOnline() ? target.getPlayer() : null,
                player.getName(),
                amount,
                TransactionType.DEPOSIT
        );

        message.reply(MessageValue.get(MessageValue::sendedMoneyDiscord)
                .replace("$coins", payActionDiscord.valueFormated())
                .replace("$player", player.getName())
                .replace("$discord", payActionDiscord.targetDiscordName())
        ).queue(message1 -> message1.addReaction("\uD83D\uDC99").queue());

    }

}

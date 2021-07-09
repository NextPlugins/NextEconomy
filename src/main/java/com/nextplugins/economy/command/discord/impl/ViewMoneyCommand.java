package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.DateFormatUtil;
import com.nextplugins.economy.util.NumberUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class ViewMoneyCommand implements Command {

    private final AccountStorage accountStorage;

    @Override
    public void execute(Message message, String[] args) {

        OfflinePlayer player = null;

        val mentionedMembers = message.getMentionedMembers();
        if (mentionedMembers.size() > 1) {

            val member = mentionedMembers.get(0);
            if (!member.getUser().isBot()) {

                val uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(member.getId());
                if (uuid == null) {

                    message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                            " Este usuário não vinculou a conta no servidor."
                    ).queue();
                    return;

                }

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
                    " O jogador informado não foi encontrado na tabela de dados."
            ).queue();
            return;

        }

        val name = account.getUsername();

        val embedBuilder = new EmbedBuilder()
                .setTitle(DiscordValue.get(DiscordValue::embedTitle).replace("$player", name))
                .setImage(DiscordValue.get(DiscordValue::embedImage).replace("$player", name))
                .setFooter(
                        DiscordValue.get(DiscordValue::embedFooter).replace("$player", name),
                        DiscordValue.get(DiscordValue::embedFooterImage).replace("$player", name)
                )
                .setColor(ColorUtil.getColorByHex(DiscordValue.get(DiscordValue::embedColor)));

        if (DiscordValue.get(DiscordValue::embedDate)) {
            embedBuilder.setTimestamp(Instant.now());
        }

        val receiveCoins = account.isReceiveCoins()
                ? DiscordValue.get(DiscordValue::successEmoji) + " Habilitado"
                : DiscordValue.get(DiscordValue::errorEmoji) + " Desativado";

        val accountBankHistoric = account.getTransactions().isEmpty() ? null
                : account.getTransactions().get(0);

        var transaction = "Este jogador nunca fez/recebeu uma transação";
        if (accountBankHistoric != null) {

            val typeMessage = accountBankHistoric.getType() == TransactionType.WITHDRAW ? "Enviou" : "Recebeu";
            val sendMessage = accountBankHistoric.getType() == TransactionType.WITHDRAW ? "para" : "de";

            transaction = String.format(
                    "%s %s %s %s em %s",
                    typeMessage,
                    NumberUtils.format(accountBankHistoric.getAmount()),
                    sendMessage,
                    accountBankHistoric.getTarget(),
                    DateFormatUtil.of(accountBankHistoric.getMilli())
            );

        }

        val section = DiscordValue.get(DiscordValue::embedFields);
        for (String key : section.getKeys(false)) {

            val keySection = section.getConfigurationSection(key);
            val inline = keySection.getBoolean("inline", false);

            if (keySection.getBoolean("blank", false)) {

                embedBuilder.addBlankField(inline);
                continue;

            }

            var title = keySection.getString("title", null);
            if (title != null) title = title
                    .replace("$coinName", MessageValue.get(MessageValue::coinsCurrency))
                    .replace("$player", account.getUsername());

            var text = keySection.getString("text", null);
            if (text != null) text = text.replace("$player", account.getUsername())
                    .replace("$money", account.getBalanceFormated())
                    .replace("$coinName", MessageValue.get(MessageValue::coinsCurrency))
                    .replace("$transactionsmoney", NumberUtils.format(account.getMovimentedBalance()))
                    .replace("$transactions", String.valueOf(account.getTransactionsQuantity()))
                    .replace("$abletoCoins", receiveCoins)
                    .replace("$lastTransactionMessage", transaction);

            embedBuilder.addField(title, text, inline);

        }

        message.reply(embedBuilder.build()).queue();

    }
}

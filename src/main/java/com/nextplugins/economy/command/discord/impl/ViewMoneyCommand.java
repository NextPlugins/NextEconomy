package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.NextEconomy;
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
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
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
public class ViewMoneyCommand implements Command {

    private final AccountStorage accountStorage = NextEconomy.getInstance().getAccountStorage();

    @Override
    public void execute(Message message, String[] args) {
        OfflinePlayer player = null;
        User user = null;

        val mentionedMembers = message.getMentionedUsers();
        if (!mentionedMembers.isEmpty()) {

            user = mentionedMembers.get(0);
            if (!user.isBot()) {

                val uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
                if (uuid == null) {

                    message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                            " Este usuário não vinculou a conta no servidor."
                    ).queue();
                    return;

                }

                player = Bukkit.getOfflinePlayer(uuid);

            }

        } else if (args.length > 0 && !args[0].equals("")) {

            val memberName = args[0];
            try {
                val id = Long.parseLong(memberName);

                val member = message.getGuild().getMemberById(id);
                if (member == null) throw new Exception();

                user = member.getUser();
                val uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
                if (uuid == null) {

                    message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                            " Este usuário não vinculou a conta no servidor."
                    ).queue();
                    return;

                }

                player = Bukkit.getOfflinePlayer(uuid);

            } catch (Exception exception) {
                player = Bukkit.getOfflinePlayer(memberName);
            }


        }

        if (player == null || !player.hasPlayedBefore()) {

            message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                    " Você precisa mencionar um usuário (mencionar ou id), ou inserir um nick válido."
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

        if (user == null) {
            val discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
            if (discordId != null) user = message.getJDA().getUserById(discordId);
        }

        val name = account.getUsername();

        val embedBuilder = new EmbedBuilder()
                .setTitle(DiscordValue.get(DiscordValue::viewMoneyTitle).replace("$player", name))
                .setThumbnail(DiscordValue.get(DiscordValue::viewMoneyImage).replace("$player", name))
                .setFooter(
                        DiscordValue.get(DiscordValue::viewMoneyFooter).replace("$player", name),
                        DiscordValue.get(DiscordValue::viewMoneyFooterImage).replace("$player", name)
                )
                .setColor(ColorUtil.getColorByHex(DiscordValue.get(DiscordValue::viewMoneyColor)));

        if (DiscordValue.get(DiscordValue::viewMoneyDate)) {
            embedBuilder.setTimestamp(Instant.now());
        }

        val receiveCoins = account.isReceiveCoins()
                ? DiscordValue.get(DiscordValue::successEmoji) + " Habilitado"
                : DiscordValue.get(DiscordValue::errorEmoji) + " Desativado";

        AccountBankHistoric accountBankHistoric = account.getTransactions().isEmpty() ? null
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

        val section = DiscordValue.get(DiscordValue::viewMoneyFields);
        for (val key : section.getKeys(false)) {

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
                    .replace("$userTag", user == null ? "Não vinculado" : user.getAsMention() + " (" + user.getAsTag() + ")")
                    .replace("$money", account.getBalanceFormated())
                    .replace("$coinName", MessageValue.get(MessageValue::coinsCurrency))
                    .replace("$transactionsmoney", NumberUtils.format(account.getMovimentedBalance()))
                    .replace("$transactions", String.valueOf(account.getTransactionsQuantity()))
                    .replace("$ableToCoins", receiveCoins)
                    .replace("$lastTransactionMessage", transaction);

            embedBuilder.addField(title, text, inline);

        }

        message.reply(embedBuilder.build()).queue();
    }
}

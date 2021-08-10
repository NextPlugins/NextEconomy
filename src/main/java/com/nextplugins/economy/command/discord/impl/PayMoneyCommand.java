package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.discord.PayActionDiscord;
import com.nextplugins.economy.api.model.discord.manager.PayActionDiscordManager;
import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.NumberUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import lombok.AllArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@AllArgsConstructor
public class PayMoneyCommand implements Command {

    private final AccountStorage accountStorage;
    private final PayActionDiscordManager payActionDiscordManager;

    @Override
    public void execute(Message message, String[] args) {

        OfflinePlayer player = null;
        User user = null;

        val mentionedMembers = message.getMentionedMembers();
        if (mentionedMembers.size() > 1) {

            user = mentionedMembers.get(0).getUser();
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

        if (player == null) {

            message.reply(DiscordValue.get(DiscordValue::invalidEmoji) +
                    " Você precisa mencionar um usuário, ou inserir um nick válido."
            ).queue();
            return;

        }

        if (args.length < 2 || args[1].equalsIgnoreCase("")) {

            message.reply(DiscordValue.get(DiscordValue::errorEmoji) + " Você precisa inserir o valor que quer enviar").queue();
            return;

        }

        val uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(message.getAuthor().getId());
        if (uuid == null) {
            message.reply(MessageValue.get(MessageValue::linkDiscord)).queue();
            return;
        }

        val offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) {
            message.reply(MessageValue.get(MessageValue::linkError)).queue();
            return;
        }

        if (offlinePlayer.getName().equals(player.getName())) {
            message.reply(MessageValue.get(MessageValue::samePersonDiscord)).queue();
            return;
        }

        val account = accountStorage.findAccount(offlinePlayer);
        if (account == null) {

            message.reply(MessageValue.get(MessageValue::linkDiscord)).queue();
            return;

        }

        if (accountStorage.findAccount(player) == null) {

            message.reply(MessageValue.get(MessageValue::invalidAccountDiscord)).queue();
            return;

        }

        val value = NumberUtils.parse(args[1]);
        if (!account.hasAmount(value)) {

            message.reply(MessageValue.get(MessageValue::noCoinsDiscord)).queue();
            return;

        }

        val format = NumberUtils.format(value);
        val targetDiscordName = user == null ? "Discord não vinculado" : user.getAsTag();
        OfflinePlayer finalPlayer = player;
        message.reply(MessageValue.get(MessageValue::sendMoneyRequestDiscord)
                .replace("$coins", format)
                .replace("$player", player.getName())
                .replace("$discord", targetDiscordName)
        ).queue(message1 -> {
            message1.addReaction("✅").queue();
            message1.addReaction("❌").queue();

            val actionDiscord = new PayActionDiscord(
                    offlinePlayer,
                    finalPlayer,
                    targetDiscordName,
                    value,
                    format
            );

            payActionDiscordManager.getActions().put(message1.getIdLong(), actionDiscord);
        });


    }

}

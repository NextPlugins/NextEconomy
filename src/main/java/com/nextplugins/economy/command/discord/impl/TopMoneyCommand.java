package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.command.discord.Command;
import com.nextplugins.economy.configuration.DiscordValue;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import lombok.val;
import lombok.var;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class TopMoneyCommand implements Command {

    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    @Override
    public void execute(Message event, String[] args) {


        val embedBuilder = new EmbedBuilder();
        embedBuilder.set

        for (Account account : rankingStorage.getRankByCoin()) {

        }

    }

}

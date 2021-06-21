package com.nextplugins.economy.command.discord.impl;

import com.nextplugins.economy.command.discord.Command;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.val;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class ViewMoneyCommand implements Command {

    @Override
    public void execute(DiscordGuildMessageReceivedEvent event, String[] args) {

        val message = event.getMessage();
        val mentionedMembers = message.getMentionedMembers();

    }
}

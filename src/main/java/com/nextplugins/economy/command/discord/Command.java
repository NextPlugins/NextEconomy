package com.nextplugins.economy.command.discord;


import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;

public interface Command {

    void execute(DiscordGuildMessageReceivedEvent event, String[] args);

}

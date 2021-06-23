package com.nextplugins.economy.command.discord;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.Data;
import lombok.val;

@Data
public final class CommandHandler {

    private final CommandMap commandMap;

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onGuildMessageReceived(DiscordGuildMessageReceivedEvent event) {

        val contentDisplay = event.getMessage().getContentDisplay();

        val commands = commandMap.getCommands();
        val args = contentDisplay.split(" ");

        val command = commands.getOrDefault(args[0], null);
        if (command == null) return;

        command.execute(event.getMessage(), args[1].split(" "));

    }

}

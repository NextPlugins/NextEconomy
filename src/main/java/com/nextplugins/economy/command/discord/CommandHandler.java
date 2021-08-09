package com.nextplugins.economy.command.discord;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.Data;
import lombok.val;

@Data
public final class CommandHandler {

    private final CommandMap commandMap;

    @Subscribe
    public void onGuildMessageReceived(DiscordGuildMessageReceivedEvent event) {

        val contentDisplay = event.getMessage().getContentDisplay();

        val commands = commandMap.getCommands();
        val args = contentDisplay.split(" ");

        val command = commands.getOrDefault(args[0], null);
        if (command == null) return;


        val stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i + 1 < args.length) stringBuilder.append(" ");
        }

        command.execute(event.getMessage(), stringBuilder.toString().split(" "));

    }

}

package com.nextplugins.economy.command.discord;

import com.nextplugins.economy.NextEconomy;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import lombok.Data;
import lombok.val;

@Data
public final class CommandHandler {

    private final CommandMap commandMap;

    @Subscribe
    public void onGuildMessageReceived(DiscordGuildMessageReceivedEvent event) {

        val logger = NextEconomy.getInstance().getLogger();
        logger.info("Testing 1");

        val contentDisplay = event.getMessage().getContentDisplay();

        logger.info("Message -: " + contentDisplay);

        val commands = commandMap.getCommands();
        val args = contentDisplay.split(" ");

        for (int i = 0; i < args.length; i++) {
            logger.info("Part " + i + " " + args[i]);
        }

        val command = commands.getOrDefault(args[0], null);
        if (command == null) return;

        val stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
        }

        command.execute(event.getMessage(), stringBuilder.toString().split(" "));

    }

}

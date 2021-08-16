package com.nextplugins.economy.command.discord;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;

public interface Command {

    void execute(Message message, String[] args);

}

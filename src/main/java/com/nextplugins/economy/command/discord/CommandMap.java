package com.nextplugins.economy.command.discord;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public final class CommandMap {

    private final String prefix;

    private Map<String, Command> commands = new HashMap<>();

    public void register(String key, Command value) {
        if (!key.startsWith(prefix)) key = prefix + key;

        commands.put(key, value);
    }

}
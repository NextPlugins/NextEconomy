package com.nextplugins.economy.api.model.discord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class PayActionDiscord {

    private final OfflinePlayer player;
    private final double value;
    private final String valueFormated;


}

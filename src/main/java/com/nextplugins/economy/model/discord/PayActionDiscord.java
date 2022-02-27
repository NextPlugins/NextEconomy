package com.nextplugins.economy.model.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.OfflinePlayer;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Getter
@Accessors(fluent = true)
@Builder
public class PayActionDiscord {

    private final OfflinePlayer player;
    private final OfflinePlayer target;

    private final long messageId;
    private final long textChannelId;
    private final long userId;

    private final String targetDiscordName;
    private final String valueFormated;

    private final double value;

    public RestAction<Message> getMessage() {
        val jda = DiscordSRV.getPlugin().getJda();
        val textChannelById = jda.getTextChannelById(textChannelId);
        if (textChannelById == null) return null;

        return textChannelById.retrieveMessageById(messageId);
    }

}
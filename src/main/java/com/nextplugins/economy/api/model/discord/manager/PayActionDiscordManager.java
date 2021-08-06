package com.nextplugins.economy.api.model.discord.manager;

import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.discord.PayActionDiscord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@AllArgsConstructor
public class PayActionDiscordManager extends ListenerAdapter {

    private final AccountStorage accountStorage;
    @Getter private final Map<Long, PayActionDiscord> actions = new HashMap<>();

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

        if (!actions.containsKey(event.getMessageIdLong())) return;

        val payActionDiscord = actions.get(event.getMessageIdLong());

        val account = accountStorage.findAccount(payActionDiscord.player());
        if (account == null) return;

        if (event.getReactionEmote().getAsReactionCode().equalsIgnoreCase("✅")) confirm();
        else deny();

    }

    public void confirm() {

    }

    public void deny() {

    }

}

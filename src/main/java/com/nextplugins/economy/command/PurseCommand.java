package com.nextplugins.economy.command;

import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class PurseCommand {

    @Command(
            name = "bolsa",
            aliases = { "bolsadevalores", "purse", "bag", "acoes" },
            target = CommandTarget.PLAYER,
            async = true
    )
    public void onPurseCommand(Context<Player> context) {

        if (!PurseAPI.isAvaliable()) {

            context.sendMessage(ColorUtil.colored("&cSistema desativado."));
            return;

        }

        // uau
        context.getSender().performCommand("money");

    }

    @Command(
            name = "setbolsa",
            aliases = { "setbolsa", "setbag" },
            usage = "setbolsa <porcentagem>. Exemplo: /setbolsa 150",
            permission = "nexteconomy.setpurse",
            target = CommandTarget.ALL,
            async = true
    )
    public void onSetPurseCommand(Context<CommandSender> context, int percentage) {

        val instance = PurseAPI.getInstance();
        if (instance == null) {

            context.sendMessage(ColorUtil.colored("&cSistema desativado."));
            return;

        }

        instance.updatePurse(percentage);

    }

}

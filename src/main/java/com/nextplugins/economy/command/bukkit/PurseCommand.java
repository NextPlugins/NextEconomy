package com.nextplugins.economy.command.bukkit;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.event.operations.MoneyGiveEvent;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class PurseCommand {

    @Command(
            name = "bolsa",
            aliases = { "bolsadevalores", "purse", "bag", "acoes" },
            target = CommandTarget.ALL,
            async = true
    )
    public void onPurseCommand(Context<CommandSender> context) {
        if (context.getSender() instanceof ConsoleCommandSender) return;

        val purseAPI = PurseAPI.getInstance();
        if (purseAPI == null) {
            context.sendMessage(ColorUtil.colored("&cSistema desativado."));
            return;
        }

        if (!InventoryValue.get(InventoryValue::enable)) {
            context.sendMessage(MessageValue.get(MessageValue::purseChatMessage).replace("$value", purseAPI.getPurseFormatedWithIcon()));
            return;
        }

        ((Player) context.getSender()).performCommand("money");
    }

    @Command(
            name = "bolsa.give",
            aliases = { "add" },
            target = CommandTarget.ALL,
            usage = "/bolsa give <player> <amount>",
            permission = "nexteconomy.purse.give",
            async = true
    )
    public void onPurseGiveCommand(Context<CommandSender> context,
                                   OfflinePlayer player,
                                   String amount) {
        val sender = context.getSender();
        val parse = NumberUtils.parse(amount);

        if (parse < 1) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        val offlineAccount = NextEconomy.getInstance().getAccountStorage().findAccount(player);
        if (offlineAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val value = parse * PurseAPI.getInstance().getPurseMultiplier();
        val moneyGiveEvent = new MoneyGiveEvent(sender, player, value, parse);
        Bukkit.getPluginManager().callEvent(moneyGiveEvent);
    }

    @Command(
            name = "setbolsa",
            aliases = { "setbolsa", "setbag" },
            usage = "setbolsa <valor>. Exemplo: /setbolsa 150",
            permission = "nexteconomy.setpurse",
            target = CommandTarget.ALL,
            async = true
    )
    public void onSetPurseCommand(Context<CommandSender> context, int number) {
        val instance = PurseAPI.getInstance();
        if (instance == null) {
            context.sendMessage(ColorUtil.colored("&cSistema desativado."));
            return;
        }

        if (NumberUtils.isInvalid(number)) {
            context.sendMessage(ColorUtil.colored("&cValor precisa ser positivo."));
            return;
        }

        instance.updatePurse(number);
    }

}

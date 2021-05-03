package com.nextplugins.economy.command;

import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.manager.CheckManager;
import com.nextplugins.economy.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public final class CheckCommand {

    private final AccountStorage accountStorage;

    @Command(
            name = "check",
            aliases = {"cheque"},
            description = "Sistema de cheque.",
            permission = "nexteconomy.command.check",
            async = true
    )
    public void checkCommand(Context<CommandSender> context) {
        final List<String> helpMessage = MessageValue.get(MessageValue::checkHelpCommand);

        helpMessage.forEach(context::sendMessage);
    }

    @Command(
            name = "check.create",
            aliases = {"criar"},
            description = "Crie um cheque com um certo valor.",
            permission = "nexteconomy.command.check.create",
            usage = "/cheque criar (valor) [jogador]",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void createCheckCommand(Context<Player> context, String value, @Optional Player target) {
        final Player player = context.getSender();

        final double amount = NumberUtils.parse(value);

        final double minValue = FeatureValue.get(FeatureValue::checkMinimumValue);

        if (amount < minValue) {
            player.sendMessage(
                    MessageValue.get(MessageValue::checkMinimumValue)
                            .replace("$amount", NumberUtils.format(minValue))
            );
            return;
        }

        final Account account = accountStorage.findOnlineAccount(player);

        if (!account.hasAmount(amount)) {
            player.sendMessage(MessageValue.get(MessageValue::checkInsufficientValue));
            return;
        }

        account.createTransaction(
                "Cheque",
                amount,
                TransactionType.WITHDRAW
        );

        player.sendMessage(
                MessageValue.get(MessageValue::checkCreated)
                        .replace("$checkValue", NumberUtils.format(amount))
        );

        final ItemStack checkItem = CheckManager.createCheck(amount);
        if (target != null) {

            target.sendMessage(
                    MessageValue.get(MessageValue::checkReceived)
                            .replace("$checkValue", NumberUtils.format(amount))
                            .replace("$sender", player.getName())
            );

            dropItem(target, target.getInventory().addItem(checkItem));
            return;

        }

        dropItem(player, player.getInventory().addItem(checkItem));

    }

    private void dropItem(Player player, Map<Integer, ItemStack> addItem) {
        for (ItemStack itemStack : addItem.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }
    }

}

package com.nextplugins.economy.command;

import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingConfiguration;
import com.nextplugins.economy.inventory.RankingInventory;
import com.nextplugins.economy.storage.AccountStorage;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.NumberFormat;
import lombok.RequiredArgsConstructor;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class MoneyCommand {

    private final AccountStorage accountStorage;
    private final RankingStorage rankingStorage;

    @Command(
            name = "money",
            usage = "/money <jogador>",
            description = "Utilize para ver a sua quantia de Cash, ou a de outro jogador.",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void moneyCommand(Context<Player> context, @Optional OfflinePlayer target) {
        Player player = context.getSender();

        if (target == null) {
            double balance = accountStorage.getAccount(player.getUniqueId()).getBalance();

            player.sendMessage(MessageValue.get(MessageValue::seeBalance)
                    .replace("$amount", NumberFormat.format(balance))
            );
        } else {
            double targetBalance = accountStorage.getAccount(target.getUniqueId()).getBalance();

            player.sendMessage(MessageValue.get(MessageValue::seeOtherBalance)
                    .replace("$player", target.getName())
                    .replace("$amount", NumberFormat.format(targetBalance))
            );
        }

    }

    @Command(
            name = "money.pay",
            aliases = {"enviar"},
            usage = "/money enviar {jogador} {quantia}",
            description = "Utilize para enviar uma quantia da sua conta para outra.",
            permission = "nexteconomy.command.pay",
            async = true
    )
    public void moneyPayCommand(Context<Player> context, OfflinePlayer target, double amount) {
        Player player = context.getSender();

        if (target != null) {
            if (target.equals(player)) {
                player.sendMessage(MessageValue.get(MessageValue::isYourself));
                return;
            }

            Account account = accountStorage.getAccount(player.getUniqueId());
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            if (account.hasAmount(amount)) {
                targetAccount.depositAmount(amount);
                account.withdrawAmount(amount);

                player.sendMessage(
                        MessageValue.get(MessageValue::paid).replace("$player", target.getName())
                                .replace("$amount", NumberFormat.format(amount))
                );

                if (target.isOnline()) {
                    target.getPlayer().sendMessage(
                            MessageValue.get(MessageValue::received).replace("$player", player.getName())
                                    .replace("$amount", NumberFormat.format(amount))
                    );
                }
            } else {
                player.sendMessage(MessageValue.get(MessageValue::insufficientAmount));
            }
        } else {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.help",
            aliases = {"ajuda", "comandos"},
            description = "Utilize para receber ajuda com os comandos do plugin.",
            permission = "nexteconomy.command.help",
            async = true
    )
    public void moneyHelpCommand(Context<Player> context) {
        Player player = context.getSender();

        if (player.hasPermission("nexteconomy.command.help.staff")) {
            ColorUtil.colored(MessageValue.get(MessageValue::helpCommandStaff)).forEach(player::sendMessage);
        } else {
            ColorUtil.colored(MessageValue.get(MessageValue::helpCommand)).forEach(player::sendMessage);
        }
    }

    @Command(
            name = "money.set",
            aliases = {"alterar"},
            usage = "/money set {jogador} {quantia}",
            description = "Utilize para alterar a quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.set",
            async = true
    )
    public void moneySetCommand(Context<Player> context, OfflinePlayer target, double amount) {
        Player player = context.getSender();

        if (target != null) {
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            targetAccount.setBalance(amount);

            player.sendMessage(MessageValue.get(MessageValue::setAmount)
                    .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
                    .replace("$amount", NumberFormat.format(targetAccount.getBalance()))
            );
        } else {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.add",
            aliases = {"adicionar", "deposit", "depositar"},
            usage = "/money adicionar {jogador} {quantia} ",
            description = "Utilize para adicionar uma quantia de dinheiro para alguém.",
            permission = "nexteconomy.command.add",
            async = true
    )
    public void moneyAddCommand(Context<Player> context, OfflinePlayer target, double amount) {
        Player player = context.getSender();

        if (target != null) {
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            targetAccount.depositAmount(amount);

            player.sendMessage(MessageValue.get(MessageValue::addAmount)
                    .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
                    .replace("$amount", NumberFormat.format(targetAccount.getBalance()))
            );
        } else {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.remove",
            aliases = {"remover", "withdraw", "retirar"},
            usage = "/money remover {jogador} {quantia}",
            description = "Utilize para remover uma quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.add",
            async = true
    )
    public void moneyRemoveCommand(Context<Player> context, OfflinePlayer target, double amount) {
        Player player = context.getSender();

        if (target != null) {
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            targetAccount.withdrawAmount(amount);

            player.sendMessage(MessageValue.get(MessageValue::removeAmount)
                    .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
                    .replace("$amount", NumberFormat.format(targetAccount.getBalance()))
            );
        } else {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.reset",
            aliases = {"zerar"},
            usage = "/money zerar {jogador}",
            description = "Utilize para zerar a quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.reset",
            async = true
    )
    public void moneyResetCommand(Context<Player> context, OfflinePlayer target) {
        Player player = context.getSender();

        if (target != null) {
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            targetAccount.setBalance(0);

            player.sendMessage(MessageValue.get(MessageValue::resetBalance)
                    .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
            );
        } else {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.top",
            aliases = {"ranking", "podio"},
            description = "Utilize para ver os jogadores com mais dinheiro do servidor.",
            permission = "nexteconomy.command.top",
            async = true
    )
    public void moneyTopCommand(Context<Player> context) {
        Player player = context.getSender();

        String rankingType = RankingConfiguration.get(RankingConfiguration::rankingType);

        LinkedHashMap<UUID, Double> rankingAccounts = rankingStorage.getRankingAccounts();

        if (rankingType.equalsIgnoreCase("CHAT")) {
            List<String> header = RankingConfiguration.get(RankingConfiguration::chatModelHeader);
            String body = RankingConfiguration.get(RankingConfiguration::chatModelBody);
            List<String> footer = RankingConfiguration.get(RankingConfiguration::chatModelFooter);

            header.forEach(player::sendMessage);

            AtomicInteger position = new AtomicInteger(1);

            rankingAccounts.forEach((owner, balance) -> player.sendMessage(body
                    .replace("$position", String.valueOf(position.getAndIncrement()))
                    .replace("$player", Bukkit.getOfflinePlayer(owner).getName())
                    .replace("$amount", NumberFormat.format(balance))
            ));

            footer.forEach(player::sendMessage);
        } else if (rankingType.equalsIgnoreCase("INVENTORY")) {
            RankingInventory rankingInventory = new RankingInventory().init();
            rankingInventory.openInventory(player);
        } else {
            throw new IllegalArgumentException("Tipo de ranking inválido: + " + rankingType + ". (ranking.yml)");
        }

    }

    @Command(
            name = "money.npc",
            usage = "/money npc",
            description = "Utilize para ver a ajuda para os comandos do sistema de NPC.",
            permission = "nexteconomy.command.npc.help",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcCommand(Context<Player> context) {
        Player player = context.getSender();

        ColorUtil.colored(MessageValue.get(MessageValue::npcHelp)).forEach(player::sendMessage);
    }

}

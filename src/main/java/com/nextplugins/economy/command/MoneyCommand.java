package com.nextplugins.economy.command;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyGiveEvent;
import com.nextplugins.economy.api.event.operations.MoneySetEvent;
import com.nextplugins.economy.api.event.operations.MoneyWithdrawEvent;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.inventory.RankingInventory;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.util.LocationUtil;
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
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class MoneyCommand {

    private final NextEconomy plugin;
    private final AccountStorage accountStorage;
    private final RankingStorage rankingStorage;
    private final LocationManager locationManager;

    @Command(
            name = "money",
            usage = "/money <jogador>",
            description = "Utilize para ver a sua quantia de Cash, ou a de outro jogador.",
            async = true
    )
    public void moneyCommand(Context<CommandSender> context, @Optional OfflinePlayer target) {
        if (!(context.getSender() instanceof Player)) {
            context.getSender().sendMessage(MessageValue.get(MessageValue::incorrectTarget));
            return;
        }

        Player player = (Player) context.getSender();

        if (target == null) {
            double balance = accountStorage.getAccount(player.getUniqueId()).getBalance();

            player.sendMessage(MessageValue.get(MessageValue::seeBalance)
                    .replace("$amount", NumberFormat.format(balance))
            );
        } else {
            if (target.hasPlayedBefore()) {
                double targetBalance = accountStorage.getAccount(target.getUniqueId()).getBalance();

                player.sendMessage(MessageValue.get(MessageValue::seeOtherBalance)
                        .replace("$player", target.getName())
                        .replace("$amount", NumberFormat.format(targetBalance))
                );
            } else {
                player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            }
        }

    }

    @Command(
            name = "money.pay",
            aliases = {"enviar"},
            usage = "/money enviar {jogador} {quantia}",
            description = "Utilize para enviar uma quantia da sua conta para outra.",
            permission = "nexteconomy.command.pay",

            target = CommandTarget.PLAYER,
            async = true
    )
    public void moneyPayCommand(Context<Player> context, OfflinePlayer target, String amount) {
        Player player = context.getSender();

        if (target != null && target.isOnline()) {

            double parse = NumberFormat.parse(amount);
            if (parse == -1) {

                player.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                return;

            }

            TransactionRequestEvent transactionRequestEvent = new TransactionRequestEvent(player, target.getPlayer(), parse);
            Bukkit.getPluginManager().callEvent(transactionRequestEvent);
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
    public void moneyHelpCommand(Context<CommandSender> context) {
        CommandSender sender = context.getSender();

        if (sender.hasPermission("nexteconomy.command.help.staff")) {
            ColorUtil.colored(MessageValue.get(MessageValue::helpCommandStaff)).forEach(sender::sendMessage);
        } else {
            ColorUtil.colored(MessageValue.get(MessageValue::helpCommand)).forEach(sender::sendMessage);
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
    public void moneySetCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        CommandSender sender = context.getSender();

        if (target != null && target.isOnline()) {

            double parse = NumberFormat.parse(amount);
            if (parse == -1) {

                sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                return;

            }

            MoneySetEvent moneySetEvent = new MoneySetEvent(sender, target.getPlayer(), parse);
            Bukkit.getPluginManager().callEvent(moneySetEvent);
        } else {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
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
    public void moneyAddCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        CommandSender sender = context.getSender();

        if (target != null && target.isOnline()) {

            double parse = NumberFormat.parse(amount);
            if (parse == -1) {

                sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                return;

            }

            MoneyGiveEvent moneyGiveEvent = new MoneyGiveEvent(sender, target.getPlayer(), parse);
            Bukkit.getPluginManager().callEvent(moneyGiveEvent);
        } else {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
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
    public void moneyRemoveCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        CommandSender sender = context.getSender();

        if (target != null && target.isOnline()) {

            double parse = NumberFormat.parse(amount);
            if (parse == -1) {

                sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
                return;

            }

            MoneyWithdrawEvent moneyWithdrawEvent = new MoneyWithdrawEvent(sender, target.getPlayer(), parse);
            Bukkit.getPluginManager().callEvent(moneyWithdrawEvent);
        } else {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
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
    public void moneyResetCommand(Context<CommandSender> context, OfflinePlayer target) {
        CommandSender sender = context.getSender();

        if (target != null && target.isOnline()) {
            Account targetAccount = accountStorage.getAccount(target.getUniqueId());

            targetAccount.setBalance(0);

            sender.sendMessage(MessageValue.get(MessageValue::resetBalance)
                    .replace("$player", Bukkit.getOfflinePlayer(targetAccount.getOwner()).getName())
            );
        } else {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
        }

    }

    @Command(
            name = "money.top",
            aliases = {"ranking", "podio"},
            description = "Utilize para ver os jogadores com mais dinheiro do servidor.",
            permission = "nexteconomy.command.top",
            async = true
    )
    public void moneyTopCommand(Context<CommandSender> context) {
        CommandSender sender = context.getSender();

        String rankingType = RankingValue.get(RankingValue::rankingType);

        if (rankingType.equalsIgnoreCase("CHAT")) {
            List<Account> accounts = rankingStorage.getRankingAccounts();

            List<String> header = RankingValue.get(RankingValue::chatModelHeader);
            String body = RankingValue.get(RankingValue::chatModelBody);
            List<String> footer = RankingValue.get(RankingValue::chatModelFooter);

            header.forEach(sender::sendMessage);

            AtomicInteger position = new AtomicInteger(1);

            String tag = RankingValue.get(RankingValue::tycoonTagValue);

            for (Account account : accounts) {
                String name = Bukkit.getOfflinePlayer(account.getOwner()).getName();
                String balance = NumberFormat.format(account.getBalance());

                sender.sendMessage(body
                        .replace("$position", String.valueOf(position.get()))
                        .replace("$player", position.get() == 1
                                ? tag + ChatColor.GREEN + " " + name
                                : name
                        )
                        .replace("$amount", balance)
                );
                position.getAndIncrement();
            }

            footer.forEach(sender::sendMessage);
        } else if (rankingType.equalsIgnoreCase("INVENTORY")) {
            if (sender instanceof Player) {
                RankingInventory rankingInventory = new RankingInventory().init();
                rankingInventory.openInventory((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "Este tipo de ranking só pode ser usado por jogadores.");
            }
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

    @Command(
            name = "money.npc.add",
            aliases = {"npc.adicionar"},
            usage = "/money npc add {posição}",
            description = "Utilize para definir uma localização de spawn de NPC de certa posição.",
            permission = "nexteconomy.command.npc.add",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcAddCommand(Context<Player> context, int position) throws IOException {
        Player player = context.getSender();

        if (!CustomRankingRegistry.isEnabled()) {
            player.sendMessage(ChatColor.RED + "O ranking em NPC foi desabilitado por falta de dependências.");
            return;
        }

        if (position <= 0) {
            player.sendMessage(MessageValue.get(MessageValue::wrongPosition));
            return;
        }

        int limit = RankingValue.get(RankingValue::rankingLimit);

        if (position > limit) {
            player.sendMessage(MessageValue.get(MessageValue::positionReachedLimit)
                    .replace("$limit", String.valueOf(limit))
            );
            return;
        }

        if (locationManager.getLocationMap().containsKey(position)) {
            player.sendMessage(MessageValue.get(MessageValue::positionAlreadyDefined));
            return;
        }

        locationManager.getLocationMap().put(position, player.getLocation());

        List<String> locations = plugin.getNpcConfig().getStringList("npc.locations");
        locations.add(position + " " + LocationUtil.byLocationNoBlock(player.getLocation()));

        plugin.getNpcConfig().set("npc.locations", locations);
        plugin.getNpcConfig().save(plugin.getNpcFile());

        player.sendMessage(MessageValue.get(MessageValue::positionSuccessfulCreated).replace("$position", String.valueOf(position)));
        CustomRankingRegistry.getRunnable().run();

    }

    @Command(
            name = "money.npc.remove",
            aliases = {"npc.remover"},
            usage = "/money npc remove {posição}",
            description = "Utilize para remover uma localização de spawn de NPC de certa posição.",
            permission = "nexteconomy.command.npc.remove",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcRemoveCommand(Context<Player> context, int position) throws Exception {
        Player player = context.getSender();

        if (!CustomRankingRegistry.isEnabled()) {
            player.sendMessage(ChatColor.RED + "O ranking em NPC foi desabilitado por falta de dependências.");
            return;
        }

        if (position <= 0) {
            player.sendMessage(MessageValue.get(MessageValue::wrongPosition));
            return;
        }

        int limit = RankingValue.get(RankingValue::rankingLimit);

        if (position > limit) {
            player.sendMessage(MessageValue.get(MessageValue::positionReachedLimit)
                    .replace("$limit", String.valueOf(limit))
            );
            return;
        }

        if (!locationManager.getLocationMap().containsKey(position)) {
            player.sendMessage(MessageValue.get(MessageValue::positionNotYetDefined));
            return;
        }

        List<String> locations = plugin.getNpcConfig().getStringList("npc.locations");
        locations.remove(position + " " + LocationUtil.byLocationNoBlock(locationManager.getLocation(position)));

        plugin.getNpcConfig().set("npc.locations", locations);
        plugin.getNpcConfig().save(plugin.getNpcFile());

        player.sendMessage(MessageValue.get(MessageValue::positionSuccessfulRemoved).replace("$position", String.valueOf(position)));
        CustomRankingRegistry.getRunnable().run();

    }

}

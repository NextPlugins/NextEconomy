package com.nextplugins.economy.command.bukkit;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.event.operations.MoneyChangeEvent;
import com.nextplugins.economy.api.event.operations.MoneyGiveEvent;
import com.nextplugins.economy.api.event.operations.MoneySetEvent;
import com.nextplugins.economy.api.event.operations.MoneyWithdrawEvent;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.storage.AccountStorage;
import com.nextplugins.economy.ranking.CustomRankingRegistry;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.util.LocationUtil;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.NumberUtils;
import com.nextplugins.economy.views.registry.InventoryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

import static com.nextplugins.economy.util.ColorUtil.colored;

@RequiredArgsConstructor
public final class MoneyCommand {

    private final NextEconomy plugin;
    private final AccountStorage accountStorage;
    private final LocationManager locationManager;

    @Command(
            name = "coins",
            aliases = {"coin", "money", "dinheiro", "moedas"},
            description = "Abrir menu do sistema de economia",
            async = true
    )
    public void moneyCommand(Context<CommandSender> context) {
        if (context.getSender() instanceof ConsoleCommandSender) return;

        val player = (Player) context.getSender();
        if (!InventoryValue.get(InventoryValue::enable)) {
            player.performCommand("money ver");
            return;
        }

        val inventory = InventoryRegistry.getInstance().getBankView();
        inventory.openInventory(player);
    }

    @Command(
            name = "coins.ver",
            description = "Ver o dinheiro de outro jogador",
            usage = "/coins ver [jogador]",
            async = true
    )
    public void moneyViewCommand(Context<CommandSender> context, @Optional OfflinePlayer target) {
        if (target == null) {
            if (context.getSender() instanceof ConsoleCommandSender) {
                context.sendMessage(MessageValue.get(MessageValue::invalidTarget));
                return;
            }

            target = (OfflinePlayer) context.getSender();
        }

        val offlineAccount = accountStorage.findAccount(target);
        if (offlineAccount == null) {
            context.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        context.sendMessage(MessageValue.get(MessageValue::seeOtherBalance)
                .replace("$player", target.getName())
                .replace("$amount", offlineAccount.getBalanceFormated())
        );
    }

    @Command(
            name = "coins.toggle",
            aliases = {"recebimento"},
            description = "Desative/ative o recebimento de coins",
            permission = "nexteconomy.togglecoins",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void toggleMoney(Context<Player> context) {
        val account = accountStorage.findAccount(context.getSender());
        account.setReceiveCoins(!account.isReceiveCoins());

        val toggleMessage = account.isReceiveCoins()
                ? MessageValue.get(MessageValue::enabledReceiveCoins)
                : MessageValue.get(MessageValue::disabledReceiveCoins);

        context.sendMessage(MessageValue.get(MessageValue::receiveCoinsToggled)
                .replace("$toggleMessage", toggleMessage)
        );
    }

    @Command(
            name = "coins.pay",
            aliases = {"enviar"},
            usage = "/coins enviar {jogador} {quantia}",
            description = "Utilize para enviar uma quantia da sua conta para outra.",
            permission = "nexteconomy.command.pay",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void moneyPayCommand(Context<Player> context, OfflinePlayer target, String amount) {
        val player = context.getSender();

        val parse = NumberUtils.parse(amount);
        if (parse < 1) {
            player.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        val offlineAccount = accountStorage.findAccount(target);
        if (offlineAccount == null) {
            player.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        if (!player.hasPermission("nexteconony.bypass") && !offlineAccount.isReceiveCoins()) {
            player.sendMessage(MessageValue.get(MessageValue::disabledCoins));
            return;
        }

        val transactionRequestEvent = new TransactionRequestEvent(player, target, offlineAccount, parse);
        Bukkit.getPluginManager().callEvent(transactionRequestEvent);
    }

    @Command(
            name = "coins.vincular",
            aliases = {"sync"},
            description = "Vincular a conta com o discord",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void moneySyncCommand(Context<Player> context) {
        if (!NextEconomy.getInstance().getDiscordCommandRegistry().isEnabled()) {
            context.getSender().sendMessage(MessageValue.get(MessageValue::disabledDiscord));
            return;
        }

        context.getSender().performCommand("discord link");
    }

    @Command(
            name = "coins.desvincular",
            aliases = {"unsync"},
            description = "Desvincular a conta com o discord",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void moneyUnsyncCommand(Context<Player> context) {
        if (!NextEconomy.getInstance().getDiscordCommandRegistry().isEnabled()) {
            context.getSender().sendMessage(MessageValue.get(MessageValue::disabledDiscord));
            return;
        }

        context.getSender().performCommand("discord unlink");
    }

    @Command(
            name = "coins.help",
            aliases = {"ajuda", "comandos"},
            description = "Utilize para receber ajuda com os comandos do plugin.",
            async = true
    )
    public void moneyHelpCommand(Context<CommandSender> context) {
        val sender = context.getSender();
        if (sender.hasPermission("nexteconomy.command.help.staff")) {
            for (String s : MessageValue.get(MessageValue::helpCommandStaff)) {
                sender.sendMessage(s);
            }
        } else {
            for (String s : MessageValue.get(MessageValue::helpCommand)) {
                sender.sendMessage(s);
            }
        }
    }

    @Command(
            name = "coins.set",
            aliases = {"alterar", "setar"},
            usage = "/coins set {jogador} {quantia}",
            description = "Utilize para alterar a quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.set",
            async = true
    )
    public void moneySetCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        val sender = context.getSender();
        val parse = NumberUtils.parse(amount);

        if (parse < 1) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        val offlineAccount = accountStorage.findAccount(target);
        if (offlineAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val moneySetEvent = new MoneySetEvent(sender, target, parse);
        Bukkit.getPluginManager().callEvent(moneySetEvent);
    }

    @Command(
            name = "coins.add",
            aliases = {"adicionar", "deposit", "depositar", "give"},
            usage = "/coins give {jogador} {quantia} ",
            description = "Utilize para adicionar uma quantia de dinheiro para alguém.",
            permission = "nexteconomy.command.add",
            async = true
    )
    public void moneyAddCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        val sender = context.getSender();
        val parse = NumberUtils.parse(amount);

        if (parse < 1) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        val offlineAccount = accountStorage.findAccount(target);
        if (offlineAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val moneyGiveEvent = new MoneyGiveEvent(sender, target, parse, 0);
        Bukkit.getPluginManager().callEvent(moneyGiveEvent);
    }

    @Command(
            name = "coins.remove",
            aliases = {"remover", "withdraw", "retirar", "take"},
            usage = "/coins remover {jogador} {quantia}",
            description = "Utilize para remover uma quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.remove",
            async = true
    )
    public void moneyRemoveCommand(Context<CommandSender> context, OfflinePlayer target, String amount) {
        val sender = context.getSender();
        val parse = NumberUtils.parse(amount);

        if (parse < 1) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidMoney));
            return;
        }

        val offlineAccount = accountStorage.findAccount(target);
        if (offlineAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        val moneyWithdrawEvent = new MoneyWithdrawEvent(sender, target, parse);
        Bukkit.getPluginManager().callEvent(moneyWithdrawEvent);
    }

    @Command(
            name = "coins.reset",
            aliases = {"zerar", "resetar"},
            usage = "/coins reset {jogador}",
            description = "Utilize para zerar a quantia de dinheiro de alguém.",
            permission = "nexteconomy.command.reset"
    )
    public void moneyResetCommand(Context<CommandSender> context, OfflinePlayer target) {
        val sender = context.getSender();
        val offlineAccount = accountStorage.findAccount(target);

        if (offlineAccount == null) {
            sender.sendMessage(MessageValue.get(MessageValue::invalidTarget));
            return;
        }

        offlineAccount.setBalance(0);
        offlineAccount.setMovimentedBalance(0);
        offlineAccount.setTransactions(Lists.newLinkedList());
        offlineAccount.setTransactionsQuantity(0);

        sender.sendMessage(MessageValue.get(MessageValue::resetBalance)
                .replace("$player", target.getName())
        );

        if (!target.isOnline()) return;

        val player = target.getPlayer();
        val moneyChangeEvent = new MoneyChangeEvent(
                player,
                offlineAccount,
                offlineAccount.getBalance(),
                offlineAccount.getBalanceFormated()
        );

        Bukkit.getPluginManager().callEvent(moneyChangeEvent);
    }

    @Command(
            name = "coins.top",
            aliases = {"ranking", "podio"},
            description = "Utilize para ver os jogadores com mais dinheiro do servidor.",
            async = true
    )
    public void moneyTopCommand(Context<CommandSender> context) {
        val rankingStorage = plugin.getRankingStorage();
        val sender = context.getSender();

        if (rankingStorage.updateRanking(false)) {
            sender.sendMessage(colored("&aAtualizando o ranking, aguarde alguns segundos."));
            return;
        }

        val rankingType = RankingValue.get(RankingValue::rankingType);

        if (rankingType.equalsIgnoreCase("CHAT")) {
            val header = colored(RankingValue.get(RankingValue::chatModelHeader));
            val footer = colored(RankingValue.get(RankingValue::chatModelFooter));

            header.forEach(sender::sendMessage);
            sender.sendMessage(colored(plugin.getRankingChatBody().getMinecraftBodyLines()));
            footer.forEach(sender::sendMessage);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(colored("&cEste tipo de ranking não é suportado via console."));
                return;
            }

            val player = (Player) sender;
            val rankingView = InventoryRegistry.getInstance().getRankingView();
            rankingView.openInventory(player);
        }
    }

    @Command(
            name = "coins.ranking",
            usage = "/coins ranking",
            description = "Utilize para ver a ajuda para os comandos do sistema de ranking.",
            permission = "nexteconomy.command.ranking.help",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcCommand(Context<Player> context) {
        val player = context.getSender();

        for (String s : colored(MessageValue.get(MessageValue::npcHelp))) {
            player.sendMessage(s);
        }
    }

    @Command(
            name = "coins.npc.add",
            aliases = {"npc.adicionar"},
            usage = "/coins npc add {posição}",
            description = "Utilize para definir uma localização de spawn de NPC de certa posição.",
            permission = "nexteconomy.command.npc.add",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcAddCommand(Context<Player> context, int position) throws IOException {
        val player = context.getSender();

        if (!CustomRankingRegistry.getInstance().isEnabled()) {
            player.sendMessage(ChatColor.RED + "O ranking em NPC foi desabilitado por falta de dependências.");
            return;
        }

        if (position <= 0) {
            player.sendMessage(MessageValue.get(MessageValue::wrongPosition));
            return;
        }

        val limit = RankingValue.get(RankingValue::rankingLimit);

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

        val location = player.getLocation();

        // use center location (helping users xd)
        location.setX(location.getBlockX() + .5);
        location.setZ(location.getBlockZ() + .5);

        locationManager.getLocationMap().put(position, location);

        val locations = plugin.getNpcConfig().getStringList("npc.locations");
        locations.add(position + " " + LocationUtil.byLocationNoBlock(location));

        plugin.getNpcConfig().set("npc.locations", locations);
        plugin.getNpcConfig().save(plugin.getNpcFile());

        player.sendMessage(MessageValue.get(MessageValue::positionSuccessfulCreated).replace("$position", String.valueOf(position)));
        CustomRankingRegistry.getInstance().getRunnable().run();
    }

    @Command(
            name = "coins.npc.remove",
            aliases = {"npc.remover"},
            usage = "/coins npc remover {posição}",
            description = "Utilize para remover uma localização de spawn de NPC de certa posição.",
            permission = "nexteconomy.command.npc.remove",
            target = CommandTarget.PLAYER,
            async = true
    )
    public void npcRemoveCommand(Context<Player> context, int position) {
        val player = context.getSender();

        if (!CustomRankingRegistry.getInstance().isEnabled()) {
            player.sendMessage(ChatColor.RED + "O ranking em NPC foi desabilitado por falta de dependências.");
            return;
        }

        if (!locationManager.getLocationMap().containsKey(position)) {
            player.sendMessage(MessageValue.get(MessageValue::positionNotYetDefined));
            return;
        }

        try {
            val locations = plugin.getNpcConfig().getStringList("npc.locations");
            locations.remove(position + " " + LocationUtil.byLocationNoBlock(locationManager.getLocation(position)));

            plugin.getNpcConfig().set("npc.locations", locations);
            plugin.getNpcConfig().save(plugin.getNpcFile());

            locationManager.getLocationMap().remove(position);

            player.sendMessage(MessageValue.get(MessageValue::positionSuccessfulRemoved).replace("$position", String.valueOf(position)));
            CustomRankingRegistry.getInstance().getRunnable().run();
        } catch (Exception exception) {
            player.sendMessage(colored("&cOcorreu um erro ao salvar o arquivo de localizações."));
        }
    }

}

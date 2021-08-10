package com.nextplugins.economy.command.bukkit;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.backup.BackupManager;
import com.nextplugins.economy.api.backup.response.ResponseType;
import com.nextplugins.economy.api.conversor.ConversorManager;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.dao.SQLProvider;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.ranking.runnable.ArmorStandRunnable;
import com.nextplugins.economy.ranking.runnable.NPCRunnable;
import com.nextplugins.economy.util.ColorUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.annotation.Optional;
import me.saiintbrisson.minecraft.command.command.Context;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@RequiredArgsConstructor
public final class NextEconomyCommand {

    private final BackupManager backupManager;
    private final AccountStorage accountStorage;
    private final AccountRepository accountRepository;
    private final ConversorManager conversorManager;

    @Command(
            name = "nexteconomy",
            aliases = {"economy", "eco", "ne"},
            permission = "nexteconomy.admin",
            async = true
    )
    public void onCommand(Context<CommandSender> context) {
        for (String s : ColorUtil.colored(MessageValue.get(MessageValue::adminCommand))) {
            context.sendMessage(s);
        }
    }

    @Command(
            name = "nexteconomy.backup",
            permission = "nexteconomy.admin",
            async = true
    )
    public void onBackupCommand(Context<CommandSender> context,
                                @Optional String name) {

        context.sendMessage(ColorUtil.colored("&aIniciando criação do backup."));

        val backup = backupManager.createBackup(
                context.getSender(),
                name,
                accountRepository,
                false, true
        );

        if (backup.getResponseType() == ResponseType.NAME_IN_USE) {

            context.sendMessage(ColorUtil.colored(
                    "&cJá existe um backup com este nome",
                    "&a&LDICA: &fDeixe o nome vazio para gerar um backup com a data e hora atual."
            ));
            return;

        }

        if (backup.getResponseType() == ResponseType.BACKUP_IN_PROGRESS) {
            context.sendMessage(ColorUtil.colored("&cJá existe um backup em andamento."));
        }

    }

    @Command(
            name = "nexteconomy.rankingdebug",
            permission = "nexteconomy.admin",
            async = true
    )
    public void onRankingDebug(Context<CommandSender> context) {

        val pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("HolographicDisplays")) {
            context.sendMessage(ColorUtil.colored("&cO plugin HolographicDisplays precisa estar ativo para usar esta função."));
            return;
        }

        for (val world : Bukkit.getWorlds()) {
            for (val entity : world.getEntities()) {

                if (entity.getType() != EntityType.ARMOR_STAND || !entity.hasMetadata("nexteconomy")) continue;
                entity.remove();

            }
        }

        HologramsAPI.getHolograms(NextEconomy.getInstance()).forEach(Hologram::delete);

        if (pluginManager.isPluginEnabled("Citizens")) {
            for (val npc : CitizensAPI.getNPCRegistry()) {
                if (!npc.getEntity().hasMetadata("nexteconomy")) continue;
                npc.despawn();
                npc.destroy();
            }

            NPCRunnable.NPCS.clear();
        }

        context.sendMessage(ColorUtil.colored("&aTodos os NPCs, ArmorStands e Hologramas foram limpos com sucesso."));

    }

    @Command(
            name = "nexteconomy.read",
            permission = "nexteconomy.admin",
            usage = "/ne read (nome) (backup ou restaurar)",
            async = true
    )
    public void onReadBackupCommand(Context<CommandSender> context,
                                    String name,
                                    String type) {

        if (!type.equalsIgnoreCase("backup") && !type.equalsIgnoreCase("restaurar")) {

            context.sendMessage(ColorUtil.colored("&cTipo inválido."));
            return;

        }

        var folderName = type.equalsIgnoreCase("restaurar") ? "restauration" : "backups";
        var typeFancy = type.equalsIgnoreCase("restaurar") ? "ponto de restauração" : "backup";

        var fileName = name.endsWith(".json") ? name : name + ".json";

        val file = new File(NextEconomy.getInstance().getDataFolder(), folderName + "/" + fileName);
        if (!file.exists()) {

            val folder = new File(NextEconomy.getInstance().getDataFolder(), folderName);
            val files = folder.list();
            if (files == null) {

                context.sendMessage(ColorUtil.colored(
                        "&cVocê não possui nenhum " + typeFancy + " criado."
                ));
                return;

            }

            context.sendMessage(ColorUtil.colored(
                    "&cO nome do " + typeFancy + " inserido é inválido, valores válidos:",
                    "&e" + Arrays.asList(files)
            ));
            return;

        }

        context.sendMessage(ColorUtil.colored(
                "&aIniciando leitura do " + typeFancy + " &2" + fileName + "&a."
        ));

        backupManager.loadBackup(context.getSender(), file, type.equalsIgnoreCase("restaurar"), true);

    }

    @Command(
            name = "nexteconomy.converter",
            permission = "nexteconomy.admin",
            usage = "/ne converter <tomysql, tosqlite>",
            async = true
    )
    public void onConverterCommand(Context<CommandSender> context,
                                   String option) {

        if (!conversorManager.checkConversorAvailability(context.getSender())) return;

        if (!option.equalsIgnoreCase("tomysql") && !option.equalsIgnoreCase("tosqlite")) {

            context.sendMessage(ColorUtil.colored(
                    "&cConversores válidos: tomysql, tosqlite"
            ));
            return;

        }

        val stopwatch = Stopwatch.createStarted();
        conversorManager.setConverting(true);

        val jdbcUrl = NextEconomy.getInstance()
                .getSqlConnector()
                .getDatabaseType()
                .getJdbcUrl();

        if (option.equalsIgnoreCase("tomysql") && !jdbcUrl.contains("mysql")) {

            context.sendMessage(ColorUtil.colored(
                    "&cVocê precisa habilitar o mysql na config e reiniciar o servidor antes."
            ));
            return;

        } else if (option.equalsIgnoreCase("tosqlite") && !jdbcUrl.contains("sqlite")) {

            context.sendMessage(ColorUtil.colored(
                    "&cVocê precisa desabilitar o mysql na config e reiniciar o servidor antes."
            ));
            return;

        }

        val type = option.equalsIgnoreCase("tomysql") ? "SQLite" : "MySQL";
        val sqlProvider = SQLProvider.of(NextEconomy.getInstance());

        val sqlConnector = sqlProvider.setup(type);
        if (sqlConnector == null) {

            context.sendMessage(ColorUtil.colored(
                    "&cVocê não configurou a conexão do " + type + " corretamente."
            ));
            return;

        }

        val repository = new AccountRepository(new SQLExecutor(sqlConnector));

        accountStorage.getCache().synchronous().invalidateAll();

        val accounts = repository.selectAll("")
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (accounts.isEmpty()) {

            context.sendMessage(ColorUtil.colored(
                    "&cOPS! Não tem nenhum dado para converter."
            ));
            return;

        }

        conversorManager.startConversion(
                context.getSender(),
                accounts,
                "Enviando para " + type,
                stopwatch
        );

    }

}

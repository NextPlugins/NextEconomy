package com.nextplugins.economy.command.bukkit.registry;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.command.bukkit.CheckCommand;
import com.nextplugins.economy.command.bukkit.MoneyCommand;
import com.nextplugins.economy.command.bukkit.NextEconomyCommand;
import com.nextplugins.economy.command.bukkit.PurseCommand;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.PurseValue;
import lombok.Data;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;

@Data(staticConstructor = "of")
public final class CommandRegistry {

    private final NextEconomy plugin;

    public void register() {
        try {
            BukkitFrame bukkitFrame = new BukkitFrame(plugin);

            bukkitFrame.registerCommands(
                    new MoneyCommand(
                            plugin,
                            plugin.getAccountStorage(),
                            plugin.getLocationManager()
                    ),
                    new NextEconomyCommand(
                            plugin.getBackupManager(),
                            plugin.getRankingStorage(),
                            plugin.getAccountStorage(),
                            plugin.getConversorManager()
                    )
            );

            if (FeatureValue.get(FeatureValue::checkSystemEnabled)) {
                bukkitFrame.registerCommands(
                        new CheckCommand(plugin.getAccountStorage())
                );
            }

            if (PurseValue.get(PurseValue::enable)) {
                bukkitFrame.registerCommands(
                        new PurseCommand()
                );
            }

            MessageHolder messageHolder = bukkitFrame.getMessageHolder();

            messageHolder.setMessage(MessageType.ERROR, MessageValue.get(MessageValue::error));
            messageHolder.setMessage(MessageType.INCORRECT_TARGET, MessageValue.get(MessageValue::incorrectTarget));
            messageHolder.setMessage(MessageType.INCORRECT_USAGE, MessageValue.get(MessageValue::incorrectUsage));
            messageHolder.setMessage(MessageType.NO_PERMISSION, MessageValue.get(MessageValue::noPermission));

            plugin.getLogger().info("Comandos registrados com sucesso.");
        } catch (Throwable t) {
            t.printStackTrace();
            plugin.getLogger().severe("Não foi possível registrar os comandos.");
        }
    }

}

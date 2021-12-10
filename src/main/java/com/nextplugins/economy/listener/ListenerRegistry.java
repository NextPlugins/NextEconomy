package com.nextplugins.economy.listener;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.FeatureValue;
import com.nextplugins.economy.listener.events.chat.DefaultChatListener;
import com.nextplugins.economy.listener.events.chat.LegendChatListener;
import com.nextplugins.economy.listener.events.chat.OpeNChatListener;
import com.nextplugins.economy.listener.events.chat.UltimateChatListener;
import com.nextplugins.economy.listener.events.check.CheckInteractListener;
import com.nextplugins.economy.listener.events.operation.MoneyGiveListener;
import com.nextplugins.economy.listener.events.operation.MoneySetListener;
import com.nextplugins.economy.listener.events.operation.MoneyWithdrawListener;
import com.nextplugins.economy.listener.events.others.RankingEntityListener;
import com.nextplugins.economy.listener.events.transaction.TransactionRequestListener;
import com.nextplugins.economy.listener.events.update.PurseListener;
import com.nextplugins.economy.listener.events.update.RankingListener;
import com.nextplugins.economy.listener.events.update.TopUpdateListener;
import com.nextplugins.economy.listener.events.user.UpdateCheckerListener;
import com.nextplugins.economy.listener.events.user.UpdateNickListener;
import com.nextplugins.economy.listener.events.user.UserConnectionListener;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.Arrays;

@Data(staticConstructor = "of")
public final class ListenerRegistry {

    private final NextEconomy plugin;

    public void register() {
        val logger = plugin.getLogger();
        try {
            val pluginManager = Bukkit.getPluginManager();

            val rankingStorage = plugin.getRankingStorage();
            val accountRepository = plugin.getAccountRepository();
            val accountStorage = plugin.getAccountStorage();
            val interactionRegistry = plugin.getInteractionRegistry();

            val listeners = Arrays.asList(
                    new UpdateCheckerListener(),
                    new MoneyGiveListener(),
                    new MoneySetListener(),
                    new MoneyWithdrawListener(),
                    new PurseListener(),
                    new TransactionRequestListener(),
                    new TopUpdateListener(),
                    new RankingEntityListener(),
                    new RankingListener(
                            accountRepository,
                            rankingStorage,
                            plugin.getRankingChatBody(),
                            plugin.getGroupWrapperManager()
                    ),
                    new DefaultChatListener(interactionRegistry),
                    new CheckInteractListener(plugin.getAccountStorage())
            );

            if (FeatureValue.get(FeatureValue::quitUpdate)) {
                pluginManager.registerEvents(new UserConnectionListener(plugin.getAccountStorage()), plugin);
            }

            if (!accountStorage.isNickMode()) {
                pluginManager.registerEvents(new UpdateNickListener(plugin.getAccountStorage()), plugin);
            }

            if (pluginManager.isPluginEnabled("nChat")) {

                pluginManager.registerEvents(
                        new OpeNChatListener(rankingStorage, interactionRegistry),
                        plugin
                );

                logger.info("[Chat] Dependência 'nChat' sendo utilizada como plugin de Chat");

            } else if (pluginManager.isPluginEnabled("Legendchat")) {

                pluginManager.registerEvents(
                        new LegendChatListener(rankingStorage, interactionRegistry),
                        plugin
                );

                logger.info("[Chat] Dependência 'LegendChat' sendo utilizada como plugin de Chat");

            } else if (pluginManager.isPluginEnabled("UltimateChat")) {

                pluginManager.registerEvents(
                        new UltimateChatListener(rankingStorage, interactionRegistry),
                        plugin
                );

                logger.info("[Chat] Dependência 'UltimateChat' sendo utilizada como plugin de Chat");

            } else {

                logger.info("[Chat] Nenhum plugin compatível de chat foi encontrado (caso exista um, solicite suporte para adição do mesmo)");
                logger.info("[Chat] Usando apenas o chat padrão do minecraft, pode conter problemas");
            }

            listeners.forEach(listener -> pluginManager.registerEvents(listener, plugin));

            logger.info("Foram registrados " + listeners.size() + " listeners com sucesso!");
        } catch (Throwable t) {
            t.printStackTrace();
            logger.severe("Não foi possível registrar os listeners.");
        }
    }

}

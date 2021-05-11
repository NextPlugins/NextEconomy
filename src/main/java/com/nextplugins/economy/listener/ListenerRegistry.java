package com.nextplugins.economy.listener;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.listener.events.chat.LegendChatListener;
import com.nextplugins.economy.listener.events.chat.OpeNChatListener;
import com.nextplugins.economy.listener.events.chat.UltimateChatListener;
import com.nextplugins.economy.listener.events.check.CheckInteractListener;
import com.nextplugins.economy.listener.events.update.AsyncPurseUpdateListener;
import com.nextplugins.economy.listener.events.operation.MoneyGiveListener;
import com.nextplugins.economy.listener.events.operation.MoneySetListener;
import com.nextplugins.economy.listener.events.operation.MoneyWithdrawListener;
import com.nextplugins.economy.listener.events.transaction.TransactionRequestListener;
import com.nextplugins.economy.listener.events.update.MoneyTopUpdateListener;
import com.nextplugins.economy.listener.events.update.AsyncRankingUpdateListener;
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

            val rankingStorage = getPlugin().getRankingStorage();
            val accountRepository = getPlugin().getAccountRepository();
            val interactionRegistry = getPlugin().getInteractionRegistry();

            val listeners = Arrays.asList(
                    new MoneyGiveListener(),
                    new MoneySetListener(),
                    new MoneyWithdrawListener(),
                    new AsyncPurseUpdateListener(),
                    new TransactionRequestListener(),
                    new MoneyTopUpdateListener(),
                    new AsyncRankingUpdateListener(accountRepository, rankingStorage),
                    new OpeNChatListener(rankingStorage, interactionRegistry),
                    new LegendChatListener(rankingStorage, interactionRegistry),
                    new UltimateChatListener(rankingStorage, interactionRegistry),
                    new CheckInteractListener(plugin.getAccountStorage())
            );

            val pluginManager = Bukkit.getPluginManager();
            listeners.forEach($ -> pluginManager.registerEvents($, plugin));

            logger.info("Foram registrados " + listeners.size() + " listeners com sucesso!");
        } catch (Throwable t) {
            t.printStackTrace();
            logger.severe("Não foi possível registrar os listeners.");
        }
    }

}

package com.nextplugins.economy.listener.events.chat;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.storage.RankingStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class TycoonTagRegister implements Listener {

    protected final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(ChatMessageEvent event) {

        Player player = event.getSender();
        if (rankingStorage.getRankingAccounts().isEmpty()) return;

        Account tycoonAccount = rankingStorage.getRankingAccounts().get(0);
        if (tycoonAccount == null || !player.getUniqueId().equals(tycoonAccount.getOwner())) return;

        event.setTagValue("tycoon", RankingValue.get(RankingValue::tycoonTagValue));

    }

}
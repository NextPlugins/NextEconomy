package com.nextplugins.economy.listener.chat;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.NumberFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class TycoonTagRegister implements Listener {

    protected final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    @EventHandler
    public void onChat(ChatMessageEvent event) {
        Player player = event.getSender();

        Account tycoonAccount = rankingStorage.getRankingAccounts().get(0);

        if (player.getUniqueId().equals(tycoonAccount.getOwner())) {
            event.setTagValue("{tycoon}", NumberFormat.format(tycoonAccount.getBalance()));
        }
    }

}

package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.NumberFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class NPCRunnable implements Runnable {

    public static final List<NPC> NPC = Lists.newLinkedList();
    public static final List<Hologram> HOLOGRAM = Lists.newLinkedList();
    @Setter @Getter private static boolean enabled;

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {
        List<Account> accounts = rankingStorage.getRankingAccounts();

        if (accounts.size() <= 0) return;

        NPC.forEach(net.citizensnpcs.api.npc.NPC::destroy);
        HOLOGRAM.forEach(Hologram::delete);

        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();

        AtomicInteger position = new AtomicInteger(1);

        for (Account account : accounts) {
            if (!locationManager.getLocationMap().containsKey(position.get())) return;

            Location location = locationManager.getLocation(position.get());
            List<String> hologramLines = RankingValue.get(RankingValue::hologramLines);
            double hologramHeight = RankingValue.get(RankingValue::hologramHeight);

            if (!hologramLines.isEmpty()) {
                Location hologramLocation = location.clone().add(0, hologramHeight, 0);
                Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                for (int i = 0; i < hologramLines.size(); i++) {
                    String replacedLine = hologramLines.get(i);

                    replacedLine = replacedLine.replace("$position", String.valueOf(position.get()));
                    replacedLine = replacedLine.replace("$player", Bukkit.getOfflinePlayer(account.getOwner()).getName());
                    replacedLine = replacedLine.replace("$amount", NumberFormat.format(account.getBalance()));

                    hologram.insertTextLine(i, replacedLine);
                }

                HOLOGRAM.add(hologram);
            }

            NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", Bukkit.getOfflinePlayer(account.getOwner()).getName());
            npc.setProtected(true);
            npc.spawn(location);

            NPC.add(npc);
            position.getAndIncrement();
        }

    }

}

package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.NumberFormat;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class NPCRunnable implements Runnable {

    public static List<NPC> NPC;
    public static List<Hologram> HOLOGRAM;

    static {
        NPC = Lists.newLinkedList();
        HOLOGRAM = Lists.newLinkedList();
    }

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {
        LinkedHashMap<UUID, Double> rankingAccounts = rankingStorage.getRankingAccounts();

        if (rankingAccounts.size() <= 0) return;

        NPC.forEach(net.citizensnpcs.api.npc.NPC::destroy);
        HOLOGRAM.forEach(Hologram::delete);

        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();

        AtomicInteger position = new AtomicInteger(1);

        rankingAccounts.forEach((owner, balance) -> {

            if (!locationManager.getLocationMap().containsKey(position.get())) return;

            Location location = locationManager.getLocation(position.get());
            List<String> hologramLines = RankingValue.get(RankingValue::hologramLines);
            int hologramHeight = RankingValue.get(RankingValue::hologramHeight);

            if (!hologramLines.isEmpty()) {
                Location hologramLocation = location.clone().add(0, hologramHeight, 0);
                Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                for (int i = 0; i < hologramLines.size(); i++) {
                    String replacedLine = hologramLines.get(i);

                    replacedLine = replacedLine.replace("$position", String.valueOf(position.get()));
                    replacedLine = replacedLine.replace("$player", Bukkit.getOfflinePlayer(owner).getName());
                    replacedLine = replacedLine.replace("$amount", NumberFormat.format(balance));

                    hologram.insertTextLine(i, replacedLine);
                }

                HOLOGRAM.add(hologram);
            }

            NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", Bukkit.getOfflinePlayer(owner).getName());
            npc.setProtected(true);
            npc.spawn(location);

            NPC.add(npc);
            position.getAndIncrement();

        });

    }

}

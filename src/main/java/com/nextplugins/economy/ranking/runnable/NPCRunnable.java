package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class NPCRunnable implements Runnable {

    public static final List<NPC> NPCS = Lists.newLinkedList();
    public static final List<Hologram> HOLOGRAM = Lists.newLinkedList();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        for (NPC npc : NPCS) {
            npc.despawn();
            CitizensAPI.getNPCRegistry().deregister(npc);
        }

        HOLOGRAM.forEach(Hologram::delete);

        if (locationManager.getLocationMap().isEmpty()) return;

        val accounts = rankingStorage.getRankByCoin();
        if (accounts.isEmpty()) return;

        val position = new AtomicInteger(1);

        val hologramLines = RankingValue.get(RankingValue::hologramArmorStandLines);
        for (val account : accounts) {

            val location = locationManager.getLocation(position.get());
            if (location == null || location.getWorld() == null) {

                plugin.getLogger().warning("A localização " + position.get() + " do ranking é inválida.");
                continue;

            }

            val chunk = location.getChunk();
            if (!chunk.isLoaded()) chunk.load(true);

            if (!hologramLines.isEmpty()) {
                val hologramLocation = location.clone().add(0, 3, 0);
                val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                val format = account.getBalanceFormated();
                for (int i = 0; i < hologramLines.size(); i++) {
                    hologram.insertTextLine(i, hologramLines.get(i)
                            .replace("$position", String.valueOf(position.get()))
                            .replace("$player", account.getUsername())
                            .replace("$prefix", plugin.getGroupWrapperManager().getPrefix(account.getUsername()))
                            .replace("$amount", format)
                    );
                }

                hologram.getVisibilityManager().setVisibleByDefault(true);

                HOLOGRAM.add(hologram);
            }

            val npcRegistry = CitizensAPI.getNPCRegistry();

            val npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", account.getUsername());
            npc.setProtected(true);
            npc.spawn(location);

            NPCS.add(npc);
            position.getAndIncrement();
        }

    }

}

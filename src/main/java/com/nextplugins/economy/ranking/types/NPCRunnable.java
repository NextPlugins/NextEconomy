package com.nextplugins.economy.ranking.types;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class NPCRunnable implements Runnable {

    public static final List<Integer> NPCS = Lists.newLinkedList();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        clearPositions();

        if (locationManager.getLocationMap().isEmpty()) return;

        ArrayList<SimpleAccount> accounts = new ArrayList<>(rankingStorage.getRankByCoin().values());
        val hologramLines = RankingValue.get(RankingValue::hologramArmorStandLines);
        val nobodyLines = RankingValue.get(RankingValue::nobodyHologramLines);
        for (val entry : locationManager.getLocationMap().entrySet()) {

            val position = entry.getKey();
            val location = entry.getValue();
            if (location == null || location.getWorld() == null) continue;

            val chunk = location.getChunk();
            if (!chunk.isLoaded()) chunk.load(true);

            SimpleAccount account = position - 1 < accounts.size() ? accounts.get(position - 1) : null;
            if (account == null) {
                if (!nobodyLines.isEmpty()) {

                    val hologramLocation = location.clone().add(0, 3, 0);
                    val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                    for (int i = 0; i < nobodyLines.size(); i++) {
                        hologram.insertTextLine(i, nobodyLines.get(i).replace("$position", String.valueOf(position)));
                    }

                }
            } else {
                if (!hologramLines.isEmpty()) {
                    val hologramLocation = location.clone().add(0, 3, 0);
                    val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                    val format = account.getBalanceFormated();
                    for (int i = 0; i < hologramLines.size(); i++) {
                        hologram.insertTextLine(i, hologramLines.get(i)
                                .replace("$position", String.valueOf(position))
                                .replace("$player", account.getUsername())
                                .replace("$prefix", plugin.getGroupWrapperManager().getPrefix(account.getUsername()))
                                .replace("$amount", format)
                        );
                    }
                }
            }

            val npcRegistry = CitizensAPI.getNPCRegistry();

            val npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", account != null ? account.getUsername() : "Yuhtin");
            npc.data().set("nexteconomy", true);
            npc.setProtected(true);
            npc.spawn(location);

            NPCS.add(npc.getId());

        }

    }

    private void clearPositions() {
        for (val id : NPCS) {
            val npc = CitizensAPI.getNPCRegistry().getById(id);
            if (npc == null) continue;

            npc.despawn();
            npc.destroy();
        }

        HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        NPCS.clear();
    }

}

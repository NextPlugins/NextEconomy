package com.nextplugins.economy.ranking.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
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
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class NPCRunnable implements Runnable {

    public static final List<Integer> NPCS = Lists.newLinkedList();
    public static final List<String> HOLOGRAMS = Lists.newLinkedList();

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final boolean holographicDisplays;

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
                    if (holographicDisplays) {
                        val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                        for (val nobodyLine : nobodyLines) {
                            hologram.appendTextLine(nobodyLine.replace("$position", String.valueOf(position)));
                        }
                    } else {

                        val cmiHologram = new CMIHologram("NextEconomy" + position, hologramLocation);
                        for (val nobodyLine : nobodyLines) {
                            cmiHologram.addLine(nobodyLine.replace("$position", String.valueOf(position)));
                        }

                        CMI.getInstance().getHologramManager().addHologram(cmiHologram);
                        cmiHologram.update();

                        HOLOGRAMS.add("NextEconomy" + position);

                    }

                }
            } else {
                if (!hologramLines.isEmpty()) {
                    val group = plugin.getGroupWrapperManager().getGroup(account.getUsername());
                    val format = account.getBalanceFormated();
                    val hologramLocation = location.clone().add(0, 3, 0);
                    if (holographicDisplays) {
                        val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                        for (val hologramLine : hologramLines) {
                            hologram.appendTextLine(hologramLine
                                    .replace("$position", String.valueOf(position))
                                    .replace("$player", account.getUsername())
                                    .replace("$prefix", group.getPrefix())
                                    .replace("$suffix", group.getSuffix())
                                    .replace("$amount", format)
                            );
                        }
                    } else {
                        val cmiHologram = new CMIHologram("NextEconomy" + position, hologramLocation);

                        for (val hologramLine : hologramLines) {
                            cmiHologram.addLine(hologramLine
                                    .replace("$position", String.valueOf(position))
                                    .replace("$player", account.getUsername())
                                    .replace("$prefix", group.getPrefix())
                                    .replace("$suffix", group.getSuffix())
                                    .replace("$amount", format)
                            );
                        }

                        CMI.getInstance().getHologramManager().addHologram(cmiHologram);
                        cmiHologram.update();

                        HOLOGRAMS.add("NextEconomy" + position);
                    }
                }
            }

            val npcRegistry = CitizensAPI.getNPCRegistry();

            val npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            val skinName = account == null ? "Yuhtin" : plugin.getSkinsRestorerManager().getSkinName(account.getUsername());
            npc.data().set("player-skin-name", skinName);
            npc.data().set("nexteconomy", true);
            npc.setProtected(true);
            npc.spawn(location);
            npc.getEntity().setMetadata("nexteconomy", new FixedMetadataValue(NextEconomy.getInstance(), true));

            NPCS.add(npc.getId());
        }
    }

    private void clearPositions() {
        try {
            for (val npc : CitizensAPI.getNPCRegistry()) {
                if (!npc.data().has("nexteconomy")) continue;

                npc.despawn();
                npc.destroy();
            }

        } catch (Exception exception) {
            for (val id : NPCRunnable.NPCS) {
                val npc = CitizensAPI.getNPCRegistry().getById(id);
                if (npc == null) continue;

                npc.despawn();
                npc.destroy();
            }
        }

        if (holographicDisplays) HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        else {
            for (val entry : HOLOGRAMS) {
                val cmiHologram = CMI.getInstance().getHologramManager().getHolograms().get(entry);
                if (cmiHologram == null) continue;
                CMI.getInstance().getHologramManager().removeHolo(cmiHologram);
            }
        }

        HOLOGRAMS.clear();
        NPCS.clear();
    }

}

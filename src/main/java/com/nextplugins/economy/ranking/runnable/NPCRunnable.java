package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.citizensnpcs.api.CitizensAPI;
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

        val accounts = rankingStorage.getRankByCoin();
        if (accounts.isEmpty()) return;

        NPCS.forEach(NPC::destroy);
        HOLOGRAM.forEach(Hologram::delete);

        val npcRegistry = CitizensAPI.getNPCRegistry();
        val position = new AtomicInteger(1);

        val hologramLines = RankingValue.get(RankingValue::hologramLines);
        val hologramHeight = RankingValue.get(RankingValue::hologramHeight);

        for (Account account : accounts) {

            if (!locationManager.getLocationMap().containsKey(position.get())) return;

            val location = locationManager.getLocation(position.get());

            if (!hologramLines.isEmpty()) {
                val hologramLocation = location.clone().add(0, hologramHeight, 0);
                val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                for (int i = 0; i < hologramLines.size(); i++) {

                    val replacedLine = hologramLines.get(i)
                            .replace("$position", String.valueOf(position.get()))
                            .replace("$player", account.getUsername())
                            .replace("$amount", NumberUtils.format(account.getBalance()));

                    hologram.insertTextLine(i, replacedLine);

                }

                HOLOGRAM.add(hologram);
            }

            val npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", account.getUsername());
            npc.setProtected(true);
            npc.spawn(location);

            NPCS.add(npc);
            position.getAndIncrement();
        }

    }

}

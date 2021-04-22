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
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
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
        List<Account> accounts = rankingStorage.getRankByCoin();

        if (accounts.size() <= 0) return;

        NPCS.forEach(NPC::destroy);
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
                    replacedLine = replacedLine.replace("$player", account.getUserName());
                    replacedLine = replacedLine.replace("$amount", NumberUtils.format(account.getBalance()));

                    hologram.insertTextLine(i, replacedLine);
                }

                HOLOGRAM.add(hologram);
            }

            NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "");
            npc.data().set("player-skin-name", account.getUserName());
            npc.setProtected(true);
            npc.spawn(location);

            NPCS.add(npc);
            position.getAndIncrement();
        }

    }

}

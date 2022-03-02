package com.nextplugins.economy.ranking.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.LabyModModifier;
import com.github.juliarn.npc.profile.Profile;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.AnimationValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import github.scarsz.discordsrv.dependencies.commons.lang3.tuple.Pair;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public final class NPCRunnable implements Runnable, Listener {

    public static final List<String> HOLOGRAMS = Lists.newLinkedList();
    private static final Random RANDOM = new Random();

    private final NextEconomy plugin;
    private final NPCPool npcPool;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final boolean holographicDisplays;
    private final boolean animation;

    private final Random random = new Random();

    public NPCRunnable(NextEconomy plugin, boolean holographicDisplays) {
        this.plugin = plugin;
        this.holographicDisplays = holographicDisplays;

        locationManager = plugin.getLocationManager();
        rankingStorage = plugin.getRankingStorage();
        npcPool = NPCPool.builder(plugin).spawnDistance(60).actionDistance(30).tabListRemoveTicks(20).build();

        this.animation = AnimationValue.get(AnimationValue::enable);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

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
                            hologram.appendTextLine(hologramLine.replace("$position", String.valueOf(position)).replace("$player", account.getUsername()).replace("$prefix", group.getPrefix()).replace("$suffix", group.getSuffix()).replace("$amount", format));
                        }
                    } else {
                        val cmiHologram = new CMIHologram("NextEconomy" + position, hologramLocation);

                        for (val hologramLine : hologramLines) {
                            cmiHologram.addLine(hologramLine.replace("$position", String.valueOf(position)).replace("$player", account.getUsername()).replace("$prefix", group.getPrefix()).replace("$suffix", group.getSuffix()).replace("$amount", format));
                        }

                        CMI.getInstance().getHologramManager().addHologram(cmiHologram);
                        cmiHologram.update();

                        HOLOGRAMS.add("NextEconomy" + position);
                    }
                }
            }

            val skinName = account == null ? "Yuhtin" : account.getUsername();
            val profile = new Profile(skinName);
            profile.complete();

            profile.setName("");
            profile.setUniqueId(new UUID(RANDOM.nextLong(), 0));

            val npc = NPC.builder()
                .profile(profile)
                .location(location)
                .imitatePlayer(false)
                .lookAtPlayer(false)
                .build(npcPool);

            npc.visibility().queueSpawn();

            if (animation) {
                if (position == 1) {
                    final List<String> emotes = AnimationValue.get(AnimationValue::magnataEmotes);

                    final String emote = emotes.get(random.nextInt(emotes.size()));

                    executeAnimation(npc, emote);
                }

                if (position == 2) {
                    final String rageDanceRaw = AnimationValue.get(AnimationValue::rageDance);

                    executeAnimation(npc, rageDanceRaw);
                }
            }
        }
    }

    private void executeAnimation(NPC npc, String rawValue) {
        final Pair<LabyModModifier.LabyModAction, Integer> rageDance = this.animationValue(rawValue);

        if (rageDance != null) {
            Bukkit.getScheduler().runTaskAsynchronously(NextEconomy.getInstance(), () -> {
                for (val player : npc.getSeeingPlayers()) {
                    npc.labymod().queue(
                        rageDance.getLeft(),
                        rageDance.getRight()
                    ).send(player);
                }
            });
        }
    }

    private void clearPositions() {
        npcPool.getNPCs().forEach(npc -> npcPool.removeNPC(npc.getEntityId()));

        if (holographicDisplays) HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        else {
            for (val entry : HOLOGRAMS) {
                val cmiHologram = CMI.getInstance().getHologramManager().getHolograms().get(entry);
                if (cmiHologram == null) continue;
                CMI.getInstance().getHologramManager().removeHolo(cmiHologram);
            }
        }

        HOLOGRAMS.clear();
    }

    @EventHandler
    public void onInteractNPC(PlayerNPCInteractEvent event) {
        event.getPlayer().performCommand("money top");
    }

    @EventHandler
    public void onShowNPC(PlayerNPCShowEvent event) {
        if (animation) {
            final List<String> emotes = AnimationValue.get(AnimationValue::showNpcEmotes);

            final String randomEmote = emotes.get(random.nextInt(emotes.size()));

            final Pair<LabyModModifier.LabyModAction, Integer> actionData = this.animationValue(randomEmote);

            if (actionData != null) {
                event.send(event.getNPC()
                    .labymod()
                    .queue(
                        actionData.getLeft(),
                        actionData.getRight()
                    )
                );
            }
        }
    }

    private Pair<LabyModModifier.LabyModAction, Integer> animationValue(String rawValue) {
        try {
            final String[] splittedValue = rawValue.split(":");

            final LabyModModifier.LabyModAction labyModAction = LabyModModifier.LabyModAction.valueOf(splittedValue[0]);
            final int actionId = Integer.parseInt(splittedValue[1]);

            return Pair.of(labyModAction, actionId);
        } catch (Throwable t) {
            NextEconomy.getInstance().getLogger().log(
                Level.SEVERE,
                "Animation value pattern malformed. (should be: \"sticker/emote:ID\")",
                t
            );

            return null;
        }
    }

}

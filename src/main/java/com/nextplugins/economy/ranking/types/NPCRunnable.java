package com.nextplugins.economy.ranking.types;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.NpcActionController;
import com.github.juliarn.npclib.api.Platform;
import com.github.juliarn.npclib.api.event.InteractNpcEvent;
import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.bukkit.BukkitPlatform;
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.model.ranking.HologramSupportType;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.nextplugins.economy.util.ColorUtil.colored;

@Getter
public final class NPCRunnable implements Runnable, Listener {

    public static final List<String> HOLOGRAMS = Lists.newLinkedList();
    private static final Random RANDOM = new Random();

    private final NextEconomy plugin;
    private final Platform<World, Player, ItemStack, Plugin> platform;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final HologramSupportType hologramAPI;

    private final List<String> hologramLines;
    private final List<String> nobodyLines;

    private final Random random = new Random();

    public NPCRunnable(NextEconomy plugin, HologramSupportType hologramAPI) {
        this.plugin = plugin;

        this.locationManager = plugin.getLocationManager();
        this.rankingStorage = plugin.getRankingStorage();

        this.platform = BukkitPlatform.bukkitNpcPlatformBuilder()
                .extension(plugin)
                .debug(false)
                .actionController(builder -> builder
                        .flag(NpcActionController.SPAWN_DISTANCE, 15)
                        .flag(NpcActionController.IMITATE_DISTANCE, 10)
                        .flag(NpcActionController.TAB_REMOVAL_TICKS, 20))
                .worldAccessor(BukkitWorldAccessor.nameBasedAccessor())
                .build();

        this.hologramAPI = hologramAPI;

        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.hologramLines = RankingValue.get(RankingValue::hologramArmorStandLines);
        this.nobodyLines = RankingValue.get(RankingValue::nobodyHologramLines);

        platform.eventBus().subscribe(InteractNpcEvent.class, interact -> {
            Player player = interact.player();
            Bukkit.getScheduler().runTask(plugin, () -> player.performCommand("money top"));
        });
    }

    @Override
    public void run() {
        clearPositions();
        if (locationManager.getLocationMap().isEmpty()) return;

        ArrayList<SimpleAccount> accounts = new ArrayList<>(rankingStorage.getRankByCoin().values());

        for (val entry : locationManager.getLocationMap().entrySet()) {
            val position = entry.getKey();
            val location = entry.getValue();
            if (location == null || location.getWorld() == null) continue;

            val chunk = location.getChunk();
            if (!chunk.isLoaded()) chunk.load(true);

            SimpleAccount account = position - 1 < accounts.size() ? accounts.get(position - 1) : null;
            spawnHologram(account, location, position);

            val skinName = account == null ? "Yuhtin" : account.getUsername();

            platform.newNpcBuilder()
                    .position(BukkitPlatformUtil.positionFromBukkitLegacy(location))
                    .flag(Npc.HIT_WHEN_PLAYER_HITS, false)
                    .flag(Npc.LOOK_AT_PLAYER, false)
                    .flag(Npc.SNEAK_WHEN_PLAYER_SNEAKS, false)
                    .npcSettings(builder -> builder.profileResolver((player, spawnedNpc) -> CompletableFuture.completedFuture(spawnedNpc.profile())))
                    .profile(Profile.unresolved(skinName))
                    .whenComplete((npc1, throwable) -> platform.npcTracker().trackNpc(npc1.buildAndTrack()));

        }
    }

    private void spawnHologram(SimpleAccount account, Location location, Integer position) {
        Location hologramLocation = location.clone().add(0, 3, 0);
        List<String> formatedHologramLines = new ArrayList<>();

        if (account == null) {
            for (String nobodyLine : nobodyLines) {
                formatedHologramLines.add(colored(nobodyLine.replace("$position", String.valueOf(position))));
            }
        } else {
            Group group = plugin.getGroupWrapperManager().getGroup(account.getUsername());
            String format = account.getBalanceFormated();

            for (String hologramLine : hologramLines) {
                formatedHologramLines.add(colored(hologramLine
                        .replace("$position", String.valueOf(position))
                        .replace("$player", account.getUsername())
                        .replace("$prefix", group.getPrefix())
                        .replace("$suffix", group.getSuffix())
                        .replace("$amount", format)
                ));
            }
        }

        HOLOGRAMS.add(hologramAPI.getHolder().createHologram(hologramLocation, formatedHologramLines));
    }

    public void clearPositions() {
        platform.npcTracker().trackedNpcs().forEach(Npc::unlink);
        hologramAPI.getHolder().destroyHolograms(HOLOGRAMS);
    }

}

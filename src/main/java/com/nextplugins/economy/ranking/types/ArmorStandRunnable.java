package com.nextplugins.economy.ranking.types;

import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.Group;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import com.nextplugins.economy.model.ranking.HologramSupportType;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.TypeUtil;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nextplugins.economy.util.ColorUtil.colored;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class ArmorStandRunnable implements Runnable {

    public static final List<UUID> STANDS = Lists.newLinkedList();

    private static final Material[] SWORDS = new Material[]{
            Material.DIAMOND_SWORD, TypeUtil.swapLegacy("GOLDEN_SWORD", "GOLD_SWORD"),
            Material.IRON_SWORD, Material.STONE_SWORD,
            TypeUtil.swapLegacy("WOODEN_SWORD", "WOOD_SWORD")
    };

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final HologramSupportType hologramAPI;

    private final double hologramHeight;
    private final boolean isSmall;
    private final List<String> hologramLines;
    private final List<String> nobodyLines;

    public ArmorStandRunnable(NextEconomy plugin, LocationManager locationManager, RankingStorage rankingStorage, HologramSupportType hologramAPI) {
        this.plugin = plugin;
        this.locationManager = locationManager;
        this.rankingStorage = rankingStorage;
        this.hologramAPI = hologramAPI;

        this.isSmall = RankingValue.get(RankingValue::hologramFormat).equalsIgnoreCase("SMALL");
        this.hologramHeight = isSmall ? 2.15 : 3;

        this.hologramLines = RankingValue.get(RankingValue::hologramArmorStandLines);
        this.nobodyLines = RankingValue.get(RankingValue::nobodyHologramLines);
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

            val stand = location.getWorld().spawn(location, ArmorStand.class);
            stand.setVisible(false); // show only after configuration
            stand.setMetadata("nexteconomy", new FixedMetadataValue(plugin, true));
            stand.setSmall(isSmall);
            stand.setCustomNameVisible(false);
            stand.setGravity(false);
            stand.setArms(true);

            val swordNumber = Math.min(SWORDS.length, position);

            val sword = SWORDS[swordNumber - 1];
            stand.setItemInHand(new ItemStack(sword));

            val skinName = account == null ? "Yuhtin" : account.getUsername();
            stand.setHelmet(new ItemBuilder(skinName).wrap());

            stand.setChestplate(new ItemBuilder(
                    Material.LEATHER_CHESTPLATE,
                    ColorUtil.getBukkitColorByHex(RankingValue.get(RankingValue::chestplateRGB))
            ).wrap());

            stand.setLeggings(new ItemBuilder(
                    Material.LEATHER_LEGGINGS,
                    ColorUtil.getBukkitColorByHex(RankingValue.get(RankingValue::leggingsRGB))
            ).wrap());

            stand.setBoots(new ItemBuilder(
                    Material.LEATHER_BOOTS,
                    ColorUtil.getBukkitColorByHex(RankingValue.get(RankingValue::bootsRGB))
            ).wrap());

            stand.setVisible(true); // configuration finished, show stand

            STANDS.add(stand.getUniqueId());
        }
    }

    private void clearPositions() {
        hologramAPI.getHolder().destroyHolograms(null);
        STANDS.forEach(stand -> {
            Entity entity = Bukkit.getEntity(stand);
            if (entity == null) return;

            entity.remove();
        });

        STANDS.clear();
    }

    private void spawnHologram(SimpleAccount account, Location location, int position) {
        Location hologramLocation = location.clone().add(0, hologramHeight, 0);
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

        hologramAPI.getHolder().createHologram(hologramLocation, formatedHologramLines);
    }

}


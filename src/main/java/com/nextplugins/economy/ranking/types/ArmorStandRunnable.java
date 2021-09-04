package com.nextplugins.economy.ranking.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@RequiredArgsConstructor
public final class ArmorStandRunnable implements Runnable {

    public static final List<ArmorStand> STANDS = Lists.newLinkedList();
    public static final List<String> HOLOGRAMS = Lists.newLinkedList();

    private static final Material[] SWORDS = new Material[]{
            Material.DIAMOND_SWORD, TypeUtil.swapLegacy("GOLDEN_SWORD", "GOLD_SWORD"),
            Material.IRON_SWORD, Material.STONE_SWORD,
            TypeUtil.swapLegacy("WOODEN_SWORD", "WOOD_SWORD")
    };

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    private final boolean holographicDisplays;

    @Override
    public void run() {

        val visualTime = Stopwatch.createStarted();
        plugin.getLogger().info("[Ranking] Iniciando atualização de ranking visual");

        STANDS.forEach(ArmorStand::remove);
        if (holographicDisplays) HologramsAPI.getHolograms(plugin).forEach(Hologram::delete);
        else {
            for (val entry : HOLOGRAMS) {
                val cmiHologram = CMI.getInstance().getHologramManager().getHolograms().get(entry);
                if (cmiHologram == null) continue;
                CMI.getInstance().getHologramManager().removeHolo(cmiHologram);
            }
        }

        HOLOGRAMS.clear();
        STANDS.clear();

        if (locationManager.getLocationMap().isEmpty()) return;

        ArrayList<SimpleAccount> accounts = new ArrayList<>(rankingStorage.getRankByCoin().values());
        val small = RankingValue.get(RankingValue::hologramFormat).equalsIgnoreCase("SMALL");
        val height = small ? 2.15 : 3;

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

                    val hologramLocation = location.clone().add(0, height, 0);
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
                    val hologramLocation = location.clone().add(0, height, 0);

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

            val stand = location.getWorld().spawn(location, ArmorStand.class);
            stand.setVisible(false); // show only after configuration
            stand.setMetadata("nexteconomy", new FixedMetadataValue(plugin, true));
            stand.setSmall(small);
            stand.setCustomNameVisible(false);
            stand.setGravity(false);
            stand.setArms(true);

            val swordNumber = Math.min(SWORDS.length, position);

            val sword = SWORDS[swordNumber - 1];
            stand.setItemInHand(new ItemStack(sword));

            val skinName = account == null ? "Yuhtin" : plugin.getSkinsRestorerManager().getSkinName(account.getUsername());
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

            STANDS.add(stand);

        }

        plugin.getLogger().log(Level.INFO, "[Ranking] Atualização de ranking visual finalizada. ({0})", visualTime);
    }

}


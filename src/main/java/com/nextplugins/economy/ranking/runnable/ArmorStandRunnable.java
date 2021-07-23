package com.nextplugins.economy.ranking.runnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.manager.LocationManager;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@RequiredArgsConstructor
public final class ArmorStandRunnable implements Runnable {

    public static final List<ArmorStand> STANDS = Lists.newLinkedList();
    public static final List<Hologram> HOLOGRAM = Lists.newLinkedList();

    private static final Material[] SWORDS = new Material[]{
            Material.DIAMOND_SWORD, TypeUtil.getType("GOLD_SWORD"),
            Material.IRON_SWORD, Material.STONE_SWORD,
            TypeUtil.getType("WOOD_SWORD")
    };

    private final NextEconomy plugin;
    private final LocationManager locationManager;
    private final RankingStorage rankingStorage;

    @Override
    public void run() {

        val accounts = rankingStorage.getRankByCoin();
        if (accounts.isEmpty()) return;

        STANDS.forEach(ArmorStand::remove);
        HOLOGRAM.forEach(Hologram::delete);

        val position = new AtomicInteger(1);

        for (Account account : accounts) {
            if (!locationManager.getLocationMap().containsKey(position.get())) return;

            val location = locationManager.getLocation(position.get());
            if (location == null || location.getWorld() == null) {

                plugin.getLogger().warning("A localização " + position + " do ranking é inválida.");
                continue;

            }

            val chunk = location.getChunk();
            if (!chunk.isLoaded()) chunk.load(true);

            val hologramLines = RankingValue.get(RankingValue::hologramLines);
            double hologramHeight = RankingValue.get(RankingValue::hologramHeight);

            if (!hologramLines.isEmpty()) {
                val hologramLocation = location.clone().add(0, hologramHeight, 0);
                val hologram = HologramsAPI.createHologram(plugin, hologramLocation);

                val format = account.getBalanceFormated();
                for (int i = 0; i < hologramLines.size(); i++) {
                    var replacedLine = hologramLines.get(i);

                    replacedLine = replacedLine.replace("$position", String.valueOf(position.get()));
                    replacedLine = replacedLine.replace("$player", account.getUsername());
                    replacedLine = replacedLine.replace("$amount", format);

                    hologram.insertTextLine(i, replacedLine);
                }

                HOLOGRAM.add(hologram);
            }

            val stand = location.getWorld().spawn(location, ArmorStand.class);
            stand.setVisible(false); // show only after configuration
            stand.setSmall(true);
            stand.setCustomNameVisible(false);
            stand.setGravity(false);
            stand.setArms(true);

            val swordNumber = Math.min(SWORDS.length, position.get());

            val sword = SWORDS[swordNumber - 1];
            stand.setItemInHand(new ItemStack(sword));

            stand.setHelmet(new ItemBuilder(account.getUsername()).wrap());

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
            position.getAndIncrement();
        }

    }

}


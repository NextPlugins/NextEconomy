package com.nextplugins.economy.views;

import com.google.common.collect.Lists;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.ViewerConfiguration;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.simple.SimpleViewer;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import org.bukkit.ChatColor;

import java.util.List;

public final class RankingView extends SimpleInventory {

    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    public RankingView() {
        super(
                "nexteconomy.ranking.inventory",
                RankingValue.get(RankingValue::inventoryModelTitle),
                4 * 9
        );
    }

    @Override
    protected void configureViewer(SimpleViewer viewer) {

        ViewerConfiguration configuration = viewer.getConfiguration();
        configuration.backInventory("nexteconomy.main");

    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        String headDisplayName = RankingValue.get(RankingValue::inventoryModelHeadDisplayName);
        List<String> headLore = RankingValue.get(RankingValue::inventoryModelHeadLore);
        String tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);

        int position = 1;

        for (Account account : rankingStorage.getRankingAccounts()) {
            String name = account.getUserName();

            String replacedDisplayName = headDisplayName.replace("$player", position == 1
                    ? tycoonTag + ChatColor.RESET + " " + name
                    : name)
                    .replace("$amount", NumberUtils.format(account.getBalance()))
                    .replace("$position", String.valueOf(position));

            List<String> replacedLore = Lists.newArrayList();

            for (String lore : headLore) {
                replacedLore.add(
                        lore.replace("$player", position == 1
                                ? tycoonTag + ChatColor.GREEN + " " + name
                                : name)
                                .replace("$amount", NumberUtils.format(account.getBalance()))
                                .replace("$position", String.valueOf(position))
                );
            }

            int slot = position + 9;
            if (slot >= 16) slot += 2;
            if (slot == 23) break;

            editor.setItem(slot, InventoryItem.of(
                    new ItemBuilder(account.getUserName())
                            .name(replacedDisplayName)
                            .setLore(replacedLore)
                            .wrap()
            ));

            position++;
        }

        editor.setItem(27, DefaultItem.BACK.toInventoryItem(viewer));

    }

}

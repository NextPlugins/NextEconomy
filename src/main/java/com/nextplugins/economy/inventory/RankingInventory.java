package com.nextplugins.economy.inventory;

import com.google.common.collect.Lists;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.RankingValue;
import com.nextplugins.economy.storage.RankingStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

public final class RankingInventory extends PagedInventory {

    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    public RankingInventory() {
        super(
                "nexteconomy.ranking.inventory",
                RankingValue.get(RankingValue::inventoryModelTitle),
                4 * 9
        );
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {
        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();

        configuration.itemPageLimit(21);
        configuration.border(Border.of(1, 1, 2, 1));
    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        List<InventoryItemSupplier> items = Lists.newLinkedList();

        String headDisplayName = RankingValue.get(RankingValue::inventoryModelHeadDisplayName);
        List<String> headLore = RankingValue.get(RankingValue::inventoryModelHeadLore);

        int position = 1;

        for (Account account : rankingStorage.getRankingAccounts()) {
            String name = Bukkit.getOfflinePlayer(account.getOwner()).getName();
            String tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);

            String replacedDisplayName = headDisplayName.replace("$player", position == 1
                    ? tycoonTag + ChatColor.RESET + " " + name
                    : name
            )
                    .replace("$amount", NumberFormat.format(account.getBalance()))
                    .replace("$position", String.valueOf(position));

            List<String> replacedLore = Lists.newArrayList();

            for (String lore : headLore) {
                replacedLore.add(
                        lore.replace("$player", position == 1
                                ? tycoonTag + ChatColor.GREEN + " " + name
                                : name)
                                .replace("$amount", NumberFormat.format(account.getBalance()))
                                .replace("$position", String.valueOf(position))
                );
            }

            items.add(() -> InventoryItem.of(
                    new ItemBuilder(Bukkit.getOfflinePlayer(account.getOwner()).getName())
                            .name(replacedDisplayName)
                            .setLore(replacedLore)
                            .wrap()
            ));

            position++;
        }

        return items;
    }

}

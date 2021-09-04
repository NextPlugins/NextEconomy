package com.nextplugins.economy.views;

import com.google.common.collect.Lists;
import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.GroupWrapperManager;
import com.nextplugins.economy.api.model.account.SimpleAccount;
import com.nextplugins.economy.api.skins.SkinsRestorerManager;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.TimeUtils;
import lombok.val;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class RankingView extends PagedInventory {

    private final Map<String, Integer> rankingSorterType = new HashMap<>();

    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();
    private final GroupWrapperManager groupWrapperManager = NextEconomy.getInstance().getGroupWrapperManager();
    private final SkinsRestorerManager skinsRestorerManager = NextEconomy.getInstance().getSkinsRestorerManager();

    public RankingView() {
        super(
                "nexteconomy.ranking.inventory",
                RankingValue.get(RankingValue::inventoryModelTitle),
                5 * 9
        );
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {

        val configuration = viewer.getConfiguration();
        configuration.backInventory("nexteconomy.main");
        configuration.itemPageLimit(14);
        configuration.border(Border.of(1, 1, 2, 1));

    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        editor.setItem(39, sortRankingItem(viewer));
        if (InventoryValue.get(InventoryValue::enable)) editor.setItem(40, DefaultItem.BACK.toInventoryItem(viewer));
        editor.setItem(41, restTimeUpdate());
    }

    @Override
    protected void update(Viewer viewer, InventoryEditor editor) {

        super.update(viewer, editor);
        configureInventory(viewer, viewer.getEditor());

    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {
        val items = new ArrayList<InventoryItemSupplier>();
        val headLore = RankingValue.get(RankingValue::inventoryModelHeadLore);
        val tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);

        int position = 1;

        val sorter = rankingSorterType.getOrDefault(viewer.getName(), -1);

        Collection<SimpleAccount> rankingAccounts = sorter == -1
                ? rankingStorage.getRankByCoin().values()
                : rankingStorage.getRankByMovimentation();

        for (val account : rankingAccounts) {

            val name = account.getUsername();

            val group = groupWrapperManager.getGroup(name);
            val replacedDisplayName = (position == 1
                    ? RankingValue.get(RankingValue::inventoryModelHeadDisplayNameTop)
                    : RankingValue.get(RankingValue::inventoryModelHeadDisplayName))
                    .replace("$tycoonTag", tycoonTag)
                    .replace("$position", String.valueOf(position))
                    .replace("$player", name)
                    .replace("$prefix", group.getPrefix())
                    .replace("$suffix", group.getSuffix());

            List<String> replacedLore = Lists.newArrayList();

            val transactionName = account.getTransactionsQuantity() == 1
                    ? MessageValue.get(MessageValue::singularTransaction)
                    : MessageValue.get(MessageValue::pluralTransaction);

            for (val lore : headLore) {
                replacedLore.add(lore
                        .replace("$amount", account.getBalanceFormated())
                        .replace("$transactions", account.getTransactionsQuantity() + " " + transactionName)
                        .replace("$movimentation", account.getMovimentedBalanceFormated())
                        .replace("$position", String.valueOf(position))
                );
            }

            val skinName = skinsRestorerManager.getSkinName(account.getUsername());

            items.add(() -> InventoryItem.of(
                    new ItemBuilder(skinName)
                            .name(replacedDisplayName)
                            .setLore(replacedLore)
                            .wrap()
            ));

            position++;
        }

        return items;
    }

    private InventoryItem restTimeUpdate() {

        return InventoryItem.of(new ItemBuilder("MHF_QUESTION")
                .name("&bPróxima atualização")
                .setLore(
                        "&7A próxima atualização do ranking será em",
                        "&a" + TimeUtils.format(rankingStorage.getNextUpdateMillis() - System.currentTimeMillis())
                )
                .wrap()
        );

    }

    private InventoryItem sortRankingItem(Viewer viewer) {
        AtomicInteger currentFilter = new AtomicInteger(rankingSorterType.getOrDefault(viewer.getName(), -1));
        return InventoryItem.of(new ItemBuilder(Material.HOPPER)
                .name("&bOrdenar ranking")
                .setLore(
                        "&7Ordene o ranking da maneira deseja",
                        "",
                        getColorByFilter(currentFilter.get(), -1) + " Saldo",
                        getColorByFilter(currentFilter.get(), 0) + " Dinheiro movimentado",
                        "",
                        "&aClique para mudar o tipo de ordenação."
                )
                .wrap())
                .defaultCallback(event -> {

                    rankingSorterType.put(viewer.getName(), currentFilter.incrementAndGet() > 0 ? -1 : currentFilter.get());
                    event.updateInventory();

                });
    }

    private String getColorByFilter(int currentFilter, int loopFilter) {
        return currentFilter == loopFilter ? " &e▶" : "&8";
    }

}

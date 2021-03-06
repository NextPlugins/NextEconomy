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
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.ranking.storage.RankingStorage;
import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.util.NumberUtils;
import com.nextplugins.economy.util.TimeUtils;
import lombok.val;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class RankingView extends SimpleInventory {

    private final Map<String, Integer> playerRewardFilter = new HashMap<>();
    private final RankingStorage rankingStorage = NextEconomy.getInstance().getRankingStorage();

    public RankingView() {
        super(
                "nexteconomy.ranking.inventory",
                RankingValue.get(RankingValue::inventoryModelTitle),
                5 * 9
        );
    }

    @Override
    protected void configureViewer(SimpleViewer viewer) {

        ViewerConfiguration configuration = viewer.getConfiguration();
        configuration.backInventory("nexteconomy.main");

    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        List<String> headLore = RankingValue.get(RankingValue::inventoryModelHeadLore);
        String tycoonTag = RankingValue.get(RankingValue::tycoonTagValue);

        int position = 1;

        int sorter = playerRewardFilter.getOrDefault(viewer.getName(), -1);

        val rankingAccounts = sorter == -1
                ? rankingStorage.getRankByCoin()
                : rankingStorage.getRankByMovimentation();

        for (Account account : rankingAccounts) {

            String name = account.getUsername();

            String replacedDisplayName = (position == 1
                    ? RankingValue.get(RankingValue::inventoryModelHeadDisplayNameTop)
                    : RankingValue.get(RankingValue::inventoryModelHeadDisplayName))
                    .replace("$tycoonTag", tycoonTag)
                    .replace("$prefix", "")
                    .replace("$position", String.valueOf(position))
                    .replace("$player", name);

            List<String> replacedLore = Lists.newArrayList();

            String transactionName = account.getTransactions().size() == 1
                    ? MessageValue.get(MessageValue::singularTransaction)
                    : MessageValue.get(MessageValue::pluralTransaction);

            for (String lore : headLore) {
                replacedLore.add(lore
                        .replace("$amount", account.getBalanceFormated())
                        .replace("$transactions", account.getTransactions().size() + " " + transactionName)
                        .replace("$movimentation", NumberUtils.format(account.getMovimentedBalance()))
                        .replace("$position", String.valueOf(position))
                );
            }

            int slot = position + 10;
            if (slot >= 16) slot += 4;
            if (slot == 25) break;

            editor.setItem(slot, InventoryItem.of(
                    new ItemBuilder(account.getUsername())
                            .name(replacedDisplayName)
                            .setLore(replacedLore)
                            .wrap()
            ));

            position++;
        }

        editor.setItem(0, restTimeUpdate());
        editor.setItem(40, sortRankingItem(viewer));
        editor.setItem(36, DefaultItem.BACK.toInventoryItem(viewer));

    }

    @Override
    protected void update(Viewer viewer, InventoryEditor editor) {
        configureInventory(viewer, editor);
    }

    private InventoryItem restTimeUpdate() {

        return InventoryItem.of(new ItemBuilder("MHF_QUESTION")
                .name("&6Próxima atualização")
                .setLore(
                        "&7A próxima atualização do ranking será em",
                        "&e" + TimeUtils.format(rankingStorage.getNextUpdateMillis() - System.currentTimeMillis())
                )
                .wrap()
        );


    }

    private InventoryItem sortRankingItem(Viewer viewer) {
        AtomicInteger currentFilter = new AtomicInteger(playerRewardFilter.getOrDefault(viewer.getName(), -1));
        return InventoryItem.of(new ItemBuilder(Material.HOPPER)
                .name("&6Ordenar ranking")
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

                    playerRewardFilter.put(viewer.getName(), currentFilter.incrementAndGet() > 0 ? -1 : currentFilter.get());
                    event.updateInventory();

                });
    }

    private String getColorByFilter(int currentFilter, int loopFilter) {
        return currentFilter == loopFilter ? " &b▶" : "&8";
    }

}

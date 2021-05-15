package com.nextplugins.economy.views;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.enums.DefaultItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.border.Border;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.impl.ViewerConfigurationImpl;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;
import com.nextplugins.economy.api.model.account.historic.BankHistoricComparator;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.InventoryValue;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.util.DateFormatUtil;
import com.nextplugins.economy.util.NumberUtils;
import com.nextplugins.economy.views.button.InventoryButton;
import lombok.val;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public final class HistoricBankView extends PagedInventory {

    private final AccountStorage accountStorage;

    public HistoricBankView(AccountStorage accountStorage) {
        super(
                "nexteconomy.bank.historic",
                "Histórico de Transções",
                6 * 9
        );

        this.accountStorage = accountStorage;
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {

        ViewerConfigurationImpl.Paged configuration = viewer.getConfiguration();

        configuration.backInventory("nexteconomy.main");
        configuration.previousPageSlot(0);
        configuration.nextPageSlot(8);
        configuration.itemPageLimit(28);
        configuration.border(Border.of(1));

        editor.setItem(49, DefaultItem.BACK.toInventoryItem(viewer));

    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer viewer) {

        val player = viewer.getPlayer();
        val account = accountStorage.findAccount(player);

        List<InventoryItemSupplier> items = new LinkedList<>();

        account.getTransactions().sort(new BankHistoricComparator());

        for (AccountBankHistoric transaction : account.getTransactions()) {

            val name = transaction.getType() == TransactionType.WITHDRAW
                    ? player.getName() : transaction.getTarget();

            val targetName = transaction.getType() == TransactionType.WITHDRAW
                    ? transaction.getTarget() : player.getName();

            val date = DateFormatUtil.of(transaction.getMilli());
            val transactionMessage = (transaction.getType() == TransactionType.WITHDRAW
                    ? InventoryValue.get(InventoryValue::withdrawMessage)
                    : InventoryValue.get(InventoryValue::depositMessage))
                    .replace("@target", targetName)
                    .replace("@player", name);

            val item = InventoryButton.builder()
                    .materialData(new MaterialData(Material.AIR))
                    .displayName(InventoryValue.get(InventoryValue::historicDisplayName).replace("@message", transactionMessage))
                    .nickname(InventoryValue.get(InventoryValue::historicSkullName))
                    .lore(InventoryValue.get(InventoryValue::historicLore).stream()
                            .map(line -> line
                                    .replace("@player", name)
                                    .replace("@target", targetName)
                                    .replace("@date", date)
                                    .replace("@action", transaction.getType().getMessage())
                                    .replace("@message", transactionMessage)
                                    .replace("@amount", NumberUtils.format(transaction.getAmount()))
                            )
                            .collect(Collectors.toList())
                    )
                    .build();


            items.add(() -> {

                var target = transaction.getTarget();
                if (target.equalsIgnoreCase("Banco")) target = "MrSnowDK";
                if (target.equalsIgnoreCase("Cheque")) target = "Tom25W";

                return InventoryItem.of(item.getItemStack(target));

            });

        }

        return items;
    }

}

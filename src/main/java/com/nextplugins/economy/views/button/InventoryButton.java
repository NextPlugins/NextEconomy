package com.nextplugins.economy.views.button;

import com.nextplugins.economy.util.ItemBuilder;
import com.nextplugins.economy.views.button.model.ButtonType;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.List;

@Builder
@Data
public final class InventoryButton implements Cloneable {

    private final MaterialData materialData;

    private final ButtonType buttonType;

    private final String nickname;
    private final String displayName;
    private final List<String> lore;

    private final int inventorySlot;

    private ItemStack itemStack;

    public ItemStack getItemStack(String nick) {
        if (this.itemStack == null) {

            this.itemStack = new ItemBuilder(materialData.getItemType() == Material.AIR
                    ? new ItemBuilder(nick == null ? nickname : nick).wrap()
                    : materialData.toItemStack(1)
            )
                    .name(displayName)
                    .setLore(lore)
                    .changeItemMeta(meta -> meta.addItemFlags(ItemFlag.values()))
                    .wrap();

            return itemStack;

        } else return nick == null ? itemStack : updateByNick(nick);
    }

    public ItemStack updateByNick(String nick) {
        InventoryButton button = clone();

        ItemStack itemStack = button.getItemStack();
        val type = itemStack.getType().name();
        if ((type.contains("PLAYER_HEAD") || type.contains("SKULL_ITEM")) && nickname.contains("@player")) {

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(nick);

            itemStack.setItemMeta(skullMeta);

        }

        return itemStack;
    }

    public ItemStack getItemStack() {
        return getItemStack(null);
    }

    @Override
    public InventoryButton clone() {
        try {
            return (InventoryButton) super.clone();
        } catch (Exception ignored) {
            return this;
        }
    }
}

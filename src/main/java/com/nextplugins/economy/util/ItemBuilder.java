package com.nextplugins.economy.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material type) {
        this(new ItemStack(type));
    }

    public ItemBuilder(Material type, int quantity, short data) {
        this(new ItemStack(type, quantity, data));
    }

    public ItemBuilder(String textureUrl) {

        item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (textureUrl == null || textureUrl.isEmpty()) return;

        if (!textureUrl.startsWith("https://textures.minecraft.net/texture/")) {
            textureUrl = "https://textures.minecraft.net/texture/" + textureUrl;
        }

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureUrl.getBytes()), null);
        profile.getProperties().put(
                "textures",
                new Property(
                        "textures",
                        Arrays.toString(Base64.encodeBase64(
                                String.format("{textures:{SKIN:{url:\"%s\"}}}", textureUrl).getBytes()
                        ))
                )
        );

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

            item.setItemMeta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemBuilder changeItemMeta(Consumer<ItemMeta> consumer) {
        ItemMeta itemMeta = item.getItemMeta();
        consumer.accept(itemMeta);
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder changeItem(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    public ItemBuilder name(String name) {
        return changeItemMeta(it -> it.setDisplayName(ColorUtil.colored(name)));
    }

    public ItemBuilder setLore(String... lore) {
        return changeItemMeta(it -> it.setLore(Arrays.asList(ColorUtil.colored(lore))));
    }

    public ItemBuilder setLore(List<String> lore) {
        return changeItemMeta(it -> it.setLore(lore));
    }

    public ItemBuilder addLore(List<String> lore) {
        if (lore == null || lore.isEmpty()) return this;

        return changeItemMeta(meta -> {
            List<String> list = meta.getLore();
            list.addAll(lore);
            meta.setLore(list);
        });
    }

    public ItemStack wrap() {
        return item;
    }

}

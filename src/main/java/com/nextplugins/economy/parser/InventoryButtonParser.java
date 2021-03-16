package com.nextplugins.economy.parser;

import com.nextplugins.economy.inventory.button.InventoryButton;
import com.nextplugins.economy.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

public final class InventoryButtonParser {

    public InventoryButton parse(ConfigurationSection section) {
        return InventoryButton.builder()
                .displayName(ColorUtil.colored(section.getString("displayName")))
                .lore(section.getStringList("lore").stream()
                        .map(ColorUtil::colored)
                        .collect(Collectors.toList()))
                .materialData(new MaterialData(
                        Material.getMaterial(section.getString("material", "AIR")),
                        (byte) section.getInt("data", 0))
                )
                .nickname(section.getString("skullName"))
                .inventorySlot(section.getInt("inventorySlot"))
                .build();
    }

}

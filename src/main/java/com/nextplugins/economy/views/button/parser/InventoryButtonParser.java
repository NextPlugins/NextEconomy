package com.nextplugins.economy.views.button.parser;

import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.TypeUtil;
import com.nextplugins.economy.views.button.InventoryButton;
import com.nextplugins.economy.views.button.model.ButtonType;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;

import java.util.stream.Collectors;

public final class InventoryButtonParser {

    public InventoryButton parse(ConfigurationSection section) {

        val itemStack = TypeUtil.convertFromLegacy(
                section.getString("material", ""),
                (byte) section.getInt("data", 0));

        return InventoryButton.builder()
                .displayName(ColorUtil.colored(section.getString("displayName")))
                .lore(section.getStringList("lore").stream()
                        .map(ColorUtil::colored)
                        .collect(Collectors.toList()))
                .materialData(itemStack == null ? new MaterialData(Material.AIR) : itemStack.getData())
                .nickname(section.getString("skullName"))
                .inventorySlot(section.getInt("inventorySlot"))
                .buttonType(ButtonType.valueOf(section.getString("identifier")))
                .build();
    }

}

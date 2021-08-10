package com.nextplugins.economy.api.group.impl;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.group.GroupWrapper;
import com.nextplugins.economy.util.ColorUtil;
import lombok.val;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class VaultGroupWrapper implements GroupWrapper {

    private Chat chatApi;
    private Permission permissionApi;

    @Override
    public String getPrefix(String player) {

        if (chatApi == null) return "";

        try {
            return ColorUtil.colored(chatApi.getPlayerPrefix(Bukkit.getWorlds().get(0), player));
        } catch (Exception exception) {
            // boa vault !!!
        }

        if (permissionApi == null) return "";

        try {
            val primaryGroup = permissionApi.getPrimaryGroup(Bukkit.getWorlds().get(0), player);
            val prefix = chatApi.getGroupPrefix(Bukkit.getWorlds().get(0), primaryGroup);

            return prefix == null ? "" : ColorUtil.colored(prefix);
        } catch (Exception exception) {
            return "";
        }
    }

    @Override
    public void setup() {
        setupChat();
        setupPermission();
    }

    private void setupPermission() {

        RegisteredServiceProvider<Permission> permissionProvider = NextEconomy.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider == null) return;

        permissionApi = permissionProvider.getProvider();

    }

    private void setupChat() {

        RegisteredServiceProvider<Chat> chatProvider = NextEconomy.getInstance().getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider == null) return;

        chatApi = chatProvider.getProvider();

    }

}

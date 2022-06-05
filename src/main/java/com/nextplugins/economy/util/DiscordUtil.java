package com.nextplugins.economy.util;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.configuration.RankingValue;
import com.nextplugins.economy.model.account.SimpleAccount;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@NoArgsConstructor
public class DiscordUtil {

    public static boolean isEnabled() {
        return NextEconomy.getInstance().getDiscordCommandRegistry().isEnabled();
    }

    @Nullable
    public static Guild getGuild() {
        if (!isEnabled()) return null;

        val discordSRV = DiscordSRV.getPlugin();
        val jda = discordSRV.getJda();

        return jda.getGuilds().get(0);
    }

    public static boolean addDiscordRole(SimpleAccount account, int position, Guild guild) {
        if (!isEnabled()) return false;

        val roleID = position == 1
                ? RankingValue.get(RankingValue::tycoonRoleId)
                : RankingValue.get(RankingValue::tycoonRichRoleId);

        if (roleID == null || roleID == 0) return false;

        val role = guild.getRoleById(roleID);
        if (role != null) {
            val uuid = Bukkit.getOfflinePlayer(account.getUsername()).getUniqueId();

            val discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
            if (discordId == null) return false;

            guild.addRoleToMember(discordId, role).queue();
            return true;
        }

        return false;
    }

    public static boolean removeDiscordRoles(Guild guild) {
        if (!isEnabled()) return false;

        val roles = Arrays.asList(RankingValue.get(RankingValue::tycoonRoleId), RankingValue.get(RankingValue::tycoonRichRoleId));
        for (long roleID : roles) {
            if (roleID == 0) continue;

            val role = guild.getRoleById(roleID);
            if (role == null) continue;

            for (Member member : guild.getMembersWithRoles(role)) {
                guild.removeRoleFromMember(member, role).queue();
            }
        }

        return true;
    }

}

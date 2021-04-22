package com.nextplugins.economy.api.conversor;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.old.OldAccount;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.util.ActionBarUtils;
import com.nextplugins.economy.util.ColorUtil;
import com.nextplugins.economy.util.TimeUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class ConversorManager {

    private final AccountRepository accountRepository;

    private static final String CONVERSION_FORMAT = "&4&L%s &a> &fConvertido &c%s &fde &a%s &fdados em &6%s";

    private boolean converting;
    private int actionBarTaskID;

    public boolean checkConversorAvaility(CommandSender sender) {

        if (converting) {

            sender.sendMessage(ColorUtil.colored(
                    "&cVocê já está convertendo uma tabela, aguarde a finalização da mesma."
            ));
            return false;

        }

        int maxPlayers = sender instanceof Player ? 1 : 0;
        if (Bukkit.getOnlinePlayers().size() > maxPlayers) {

            sender.sendMessage(ColorUtil.colored(
                    "&cEsta função só pode ser usada com apenas você online."
            ));
            return false;

        }

        return true;

    }

    /**
     * Save to database a {@link Set} of {@link Account}
     *
     * @param sender requested the conversion (can be null)
     * @param accounts to save
     * @param conversorName name of conversor (can be null)
     * @param initial time in milliseconds that the conversion was requested
     */
    public void startConversion(@Nullable CommandSender sender,
                                @NotNull Set<OldAccount> accounts,
                                @Nullable String conversorName,
                                long initial) {

        AtomicInteger converted = new AtomicInteger();

        Bukkit.getScheduler().runTaskAsynchronously(
                NextEconomy.getInstance(),
                () -> {

                    for (OldAccount account : accounts) {

                        accountRepository.deleteOldByUUID(account.getUuid());
                        accountRepository.saveOne(account.toAccount());
                        converted.incrementAndGet();

                    }

                    if (sender != null) sender.sendMessage(ColorUtil.colored(
                            "&aConversão terminada em &2" + TimeUtils.formatTime(System.currentTimeMillis() - initial) + "&a.",
                            "&aVocê &lnão &aprecisa &areiniciar o servidor para salvar as alterações."
                    ));

                    this.converting = false;
                    Bukkit.getScheduler().cancelTask(this.actionBarTaskID);

                }
        );

        if (sender == null || sender instanceof ConsoleCommandSender || conversorName == null) return;

        Player player = (Player) sender;

        this.actionBarTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(NextEconomy.getInstance(), () -> {

            if (!player.isOnline()) return;

            String format = ColorUtil.colored(String.format(CONVERSION_FORMAT,
                    conversorName,
                    converted,
                    accounts.size(),
                    TimeUtils.formatTime(System.currentTimeMillis() - initial)
            ));

            ActionBarUtils.sendActionBar(
                    player,
                    ColorUtil.colored(format)
            );

        }, 0L, 20L).getTaskId();

    }

}

package com.nextplugins.economy.api.conversor;

import com.google.common.base.Stopwatch;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.util.ActionBarUtils;
import com.nextplugins.economy.util.ColorUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

    public boolean checkConversorAvailability(@Nullable CommandSender sender) {

        if (sender == null) return !converting && Bukkit.getOnlinePlayers().isEmpty();

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
     * @param stopwatch to get eplased time
     */
    public void startConversion(@Nullable CommandSender sender,
                                @NotNull List<Account> accounts,
                                @Nullable String conversorName,
                                Stopwatch stopwatch) {

        AtomicInteger converted = new AtomicInteger();

        Bukkit.getScheduler().runTaskAsynchronously(
                NextEconomy.getInstance(),
                () -> {

                    for (Account account : accounts) {

                        accountRepository.saveOne(account);
                        converted.incrementAndGet();

                    }

                    stopwatch.stop();

                    if (sender != null) sender.sendMessage(ColorUtil.colored(
                            "&aConversão terminada em &2" + stopwatch + "&a.",
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
                    stopwatch
            ));

            ActionBarUtils.sendActionBar(
                    player,
                    ColorUtil.colored(format)
            );

        }, 0L, 20L).getTaskId();

    }

}

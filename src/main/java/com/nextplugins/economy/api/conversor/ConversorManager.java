package com.nextplugins.economy.api.conversor;

import com.google.common.base.Stopwatch;
import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.dao.repository.AccountRepository;
import com.nextplugins.economy.util.ActionBarUtils;
import com.nextplugins.economy.util.ColorUtil;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
public class ConversorManager {

    private static final String CONVERSION_FORMAT = "&4&L%s &a> &fConvertido &c%s &fde &a%s &fdados em &6%s";
    protected final List<Conversor> conversors = new ArrayList<>();
    private final AccountRepository accountRepository;

    private boolean converting;
    private int actionBarTaskID;

    public Conversor getByName(String name) {

        return conversors.stream()
                .filter(conversor -> conversor.getConversorName().equalsIgnoreCase(name))
                .findAny()
                .orElse(null);

    }

    public String availableConversors() {
        return conversors.stream()
                .map(Conversor::getConversorName)
                .collect(Collectors.joining(","));
    }

    public void registerConversor(Conversor conversor) {
        conversors.add(conversor);
    }

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
                                @NotNull Set<Account> accounts,
                                @Nullable String conversorName,
                                Stopwatch stopwatch) {

        val converted = new AtomicInteger();
        Bukkit.getScheduler().runTaskAsynchronously(
                NextEconomy.getInstance(),
                () -> {

                    for (val account : accounts) {

                        account.setTransactions(new LinkedList<>());
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

        if (sender == null || conversorName == null) return;

        this.actionBarTaskID = Bukkit.getScheduler().runTaskTimerAsynchronously(NextEconomy.getInstance(), () -> {

            val player = sender instanceof Player ? (Player) sender : null;
            if (player != null && !player.isOnline()) return;

            val format = ColorUtil.colored(String.format(CONVERSION_FORMAT,
                    conversorName,
                    converted,
                    accounts.size(),
                    stopwatch
            ));

            if (player == null) sender.sendMessage(ColorUtil.colored(format));
            else ActionBarUtils.sendActionBar(
                    player,
                    ColorUtil.colored(format)
            );

        }, 0L, 20L).getTaskId();

    }

}

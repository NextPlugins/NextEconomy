package com.nextplugins.economy.vault;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.model.Account;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.NumberFormat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultExpansionHook implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "NextEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return NumberFormat.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return MessageValue.get(MessageValue::coinsCurrency);
    }

    @Override
    public String currencyNameSingular() {
        return MessageValue.get(MessageValue::coinCurrency);
    }

    @Override
    public boolean hasAccount(String playerName) {
        return NextEconomyAPI.getInstance().findAccountByUUID(Bukkit.getOfflinePlayer(playerName).getUniqueId()).isPresent();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return NextEconomyAPI.getInstance().findAccountByUUID(player.getUniqueId()).isPresent();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return NextEconomyAPI.getInstance().findAccountByUUID(Bukkit.getOfflinePlayer(playerName).getUniqueId()).isPresent();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return NextEconomyAPI.getInstance().findAccountByUUID(player.getUniqueId()).isPresent();
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(Bukkit.getPlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {

        Account account = NextEconomyAPI.getInstance().findAccountByUUID(player.getUniqueId()).orElse(null);
        if (account != null) return account.getBalance();

        return 0;

    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(Bukkit.getPlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {

        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName).orElse(null);
        if (account != null) return account.hasAmount(amount);

        return false;

    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName).orElse(null);

        if (account != null) {
            if (has(playerName, amount)) {
                account.withdrawAmount(amount);
                return new EconomyResponse(amount,
                        account.getBalance(),
                        EconomyResponse.ResponseType.SUCCESS,
                        "Não foi possível terminar esta operação."
                );
            } else {
                return new EconomyResponse(amount,
                        account.getBalance(),
                        EconomyResponse.ResponseType.FAILURE,
                        "Não foi possível terminar esta operação. " +
                                "(A conta requisitada não possui quantia suficiente para completar esta transação)."
                );
            }
        }

        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName).orElse(null);

        if (account != null) {
            account.depositAmount(amount);
            return new EconomyResponse(amount,
                    account.getBalance(),
                    EconomyResponse.ResponseType.SUCCESS,
                    "Não foi possível terminar esta operação."
            );
        }

        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(Bukkit.getPlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {

        Account account = NextEconomyAPI.getInstance().findAccountByUUID(player.getUniqueId()).orElse(null);
        if (account != null) return false;

        account = Account.builder()
                .owner(player.getUniqueId())
                .balance(0)
                .build();

        NextEconomy.getInstance().getAccountDAO().insertOne(account);
        return true;

    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(Bukkit.getPlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

}

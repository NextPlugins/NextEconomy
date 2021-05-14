package com.nextplugins.economy.vault;

import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.PurseValue;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import lombok.var;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomyHook implements Economy {

    private static final NextEconomyAPI API = NextEconomyAPI.getInstance();

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
        return NumberUtils.format(amount);
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
        return API.findAccountByName(playerName) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return hasAccount(player.getName());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player.getName());
    }

    @Override
    public double getBalance(String playerName) {

        val account = API.findAccountByName(playerName);
        if (account != null) return account.getBalance();

        return 0;

    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getName());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player.getName());
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {

        val account = API.getAccountStorage().findAccount(player.getName(), player.isOnline());
        if (account != null) return account.hasAmount(amount);

        return false;

    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        val account = API.getAccountStorage().findAccount(player.getName(), player.isOnline());
        if (account != null) {
            if (account.hasAmount(amount)) {

                account.createTransaction(
                        player.isOnline() ? player.getPlayer() : null,
                        null,
                        amount,
                        TransactionType.WITHDRAW
                );

                return new EconomyResponse(amount,
                        account.getBalance(),
                        EconomyResponse.ResponseType.SUCCESS,
                        "Operação realizada com sucesso."
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

        return new EconomyResponse(
                amount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Conta inválida"
        );
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        val account = API.getAccountStorage().findAccount(player.getName(), player.isOnline());
        if (account != null) {

            val purseEnabled = PurseValue.get(PurseValue::enable) && PurseValue.get(PurseValue::applyInAll);
            val purse = purseEnabled ? PurseAPI.getInstance().getPurseMultiplier() : 1;

            val newAmount = amount * purse;

            account.createTransaction(
                    player.isOnline() ? player.getPlayer() : null,
                    null,
                    newAmount,
                    TransactionType.DEPOSIT
            );

            return new EconomyResponse(newAmount,
                    account.getBalance(),
                    EconomyResponse.ResponseType.SUCCESS,
                    "Operação realizada com sucesso."
            );

        }

        return new EconomyResponse(
                amount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Conta inválida"
        );
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {

        var account = API.findAccountByName(playerName);
        if (account != null) return false;

        account = Account.createDefault(playerName);
        API.getAccountStorage().put(account);

        return true;

    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player.getName());
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player.getName());
    }

}

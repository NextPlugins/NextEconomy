package com.nextplugins.economy.vault;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.values.MessageValue;
import com.nextplugins.economy.util.NumberUtils;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
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
        return NextEconomyAPI.getInstance().findAccountByName(playerName) != null;
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

        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName);
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

        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName);
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
        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName);

        if (account != null) {
            if (has(playerName, amount)) {

                account.createTransaction(
                        MessageValue.get(MessageValue::mainAccountName),
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
        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName);

        if (account != null) {

            account.createTransaction(
                    MessageValue.get(MessageValue::mainAccountName),
                    amount,
                    TransactionType.DEPOSIT
            );

            return new EconomyResponse(amount,
                    account.getBalance(),
                    EconomyResponse.ResponseType.SUCCESS,
                    "Operação realizada com sucesso."
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

        Account account = NextEconomyAPI.getInstance().findAccountByName(playerName);
        if (account != null) return false;

        account = Account.createDefault(playerName);

        NextEconomy.getInstance().getAccountDAO().saveOne(account);
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

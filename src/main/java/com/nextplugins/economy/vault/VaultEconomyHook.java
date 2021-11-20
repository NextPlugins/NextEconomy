package com.nextplugins.economy.vault;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.PurseAPI;
import com.nextplugins.economy.api.model.account.Account;
import com.nextplugins.economy.api.model.account.storage.AccountStorage;
import com.nextplugins.economy.api.model.account.transaction.TransactionType;
import com.nextplugins.economy.configuration.MessageValue;
import com.nextplugins.economy.configuration.PurseValue;
import com.nextplugins.economy.util.NumberUtils;
import lombok.val;
import lombok.var;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

public class VaultEconomyHook extends EconomyWrapper {

    private static final AccountStorage ACCOUNT_STORAGE = NextEconomyAPI.getInstance().getAccountStorage();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "NextEconomy";
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
    public boolean hasAccount(OfflinePlayer player) {
        return ACCOUNT_STORAGE.findAccount(player) != null;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        val account = ACCOUNT_STORAGE.findAccount(player);
        return account == null ? 0 : account.getBalance();
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (NumberUtils.isInvalid(amount)) return false;

        val account = ACCOUNT_STORAGE.findAccount(player);
        if (account == null) return false;

        val purseEnabled = PurseValue.get(PurseValue::enable) && PurseValue.get(PurseValue::withdrawEnabled);
        val purse = purseEnabled ? PurseAPI.getInstance().getPurseMultiplier() : 1;

        return account.hasAmount(amount * purse);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double initialAmount) {
        if (initialAmount == 0 || NumberUtils.isInvalid(initialAmount)) {
            return new EconomyResponse(initialAmount, 0, EconomyResponse.ResponseType.FAILURE, "Valor inválido");
        }

        val account = ACCOUNT_STORAGE.findAccount(player);
        if (account != null) {
            val purseEnabled = PurseValue.get(PurseValue::enable) && PurseValue.get(PurseValue::withdrawEnabled);
            val purse = purseEnabled ? PurseAPI.getInstance().getPurseMultiplier() : 1;

            val amontWithdraw = initialAmount * purse;
            return account.createTransaction(
                    player.isOnline() ? player.getPlayer() : null,
                    null,
                    amontWithdraw,
                    initialAmount,
                    TransactionType.WITHDRAW
            );
        }

        return new EconomyResponse(
                initialAmount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Conta inválida."
        );
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double initialAmount) {
        if (initialAmount == 0 || NumberUtils.isInvalid(initialAmount)) {
            return new EconomyResponse(initialAmount, 0, EconomyResponse.ResponseType.FAILURE, "Valor inválido");
        }

        val account = ACCOUNT_STORAGE.findAccount(player);
        if (account != null) {
            val purseEnabled = PurseValue.get(PurseValue::enable) && PurseValue.get(PurseValue::applyInAll);
            val purse = purseEnabled ? PurseAPI.getInstance().getPurseMultiplier() : 1;

            val amountDeposit = initialAmount * purse;
            return account.createTransaction(
                    player.isOnline() ? player.getPlayer() : null,
                    null,
                    amountDeposit,
                    initialAmount,
                    TransactionType.DEPOSIT
            );
        }

        return new EconomyResponse(
                initialAmount,
                0,
                EconomyResponse.ResponseType.FAILURE,
                "Conta inválida"
        );
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        var account = ACCOUNT_STORAGE.findAccount(player);
        if (account != null) return false;

        account = Account.createDefault(player);
        ACCOUNT_STORAGE.put(account);

        return true;
    }

}

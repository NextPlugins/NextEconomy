package com.nextplugins.economy.configuration.values;

import com.henryfabio.minecraft.configinjector.common.annotations.ConfigField;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigFile;
import com.henryfabio.minecraft.configinjector.common.annotations.ConfigSection;
import com.henryfabio.minecraft.configinjector.common.annotations.TranslateColors;
import com.henryfabio.minecraft.configinjector.common.injector.ConfigurationInjectable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;

@Getter
@TranslateColors
@Accessors(fluent = true)
@ConfigSection("messages")
@ConfigFile("messages.yml")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageValue implements ConfigurationInjectable {

    @Getter private static final MessageValue instance = new MessageValue();

    // currency

    @ConfigField("currency.mainAccountSkin") private String mainAccountSkin;
    @ConfigField("currency.mainAccountName") private String mainAccountName;
    @ConfigField("currency.one") private String coinCurrency;
    @ConfigField("currency.more") private String coinsCurrency;

    // requests

    @ConfigField("requests.invalid-target") private String invalidTarget;
    @ConfigField("requests.invalid-money") private String invalidMoney;
    @ConfigField("request.no-time") private String noTime;

    // operations

    @ConfigField("operations.set") private String setAmount;
    @ConfigField("operations.add") private String addAmount;
    @ConfigField("operations.remove") private String removeAmount;
    @ConfigField("operations.reset") private String resetBalance;
    @ConfigField("operations.see") private String seeBalance;
    @ConfigField("operations.see-other") private String seeOtherBalance;

    // transactions

    @ConfigField("transactions.singular") private String singularTransaction;
    @ConfigField("transactions.plural") private String pluralTransaction;
    @ConfigField("transactions.paid") private String paid;
    @ConfigField("transactions.received") private String received;
    @ConfigField("transactions.is-yourself") private String isYourself;
    @ConfigField("transactions.insufficient-amount") private String insufficientAmount;
    @ConfigField("transactions.min-value") private String minValueNecessary;

    // interaction

    @ConfigField("interaction.cancelled") private String interactionCancelled;
    @ConfigField("interaction.invalid") private String interactionInvalid;
    @ConfigField("interaction.input-player") private List<String> interactionInputPlayer;
    @ConfigField("interaction.input-money") private List<String> interactionInputMoney;
    @ConfigField("interaction.confirm") private List<String> interactionConfirm;

    // purse system

    @ConfigField("purse.devalued.icon") private String devaluedIcon;
    @ConfigField("purse.devalued.message") private String devaluedMessage;

    @ConfigField("purse.valued.icon") private String valuedIcon;
    @ConfigField("purse.valued.message") private String valuedMessage;

    @ConfigField("purse.equals.icon") private String equalsIcon;
    @ConfigField("purse.equals.message") private String equalsMessage;

    @ConfigField("purse.updated.sound") private String purseUpdatedSound;
    @ConfigField("purse.updated.message") private List<String> purseUpdatedMessage;

    @ConfigField("purse.high-status") private String purseHigh;
    @ConfigField("purse.down-status") private String purseDown;

    // new money top

    @ConfigField("new-money-top.enable") private boolean enableMoneyTopMessage;
    @ConfigField("new-money-top.message") private List<String> moneyTopMessage;
    @ConfigField("new-money-top.title") private String moneyTopTitle;

    // npc ranking

    @ConfigField("npc-ranking.wrong-position") private String wrongPosition;
    @ConfigField("npc-ranking.position-already-defined") private String positionAlreadyDefined;
    @ConfigField("npc-ranking.position-successful-created") private String positionSuccessfulCreated;
    @ConfigField("npc-ranking.position-not-yet-defined") private String positionNotYetDefined;
    @ConfigField("npc-ranking.position-successful-removed") private String positionSuccessfulRemoved;
    @ConfigField("npc-ranking.position-reached-limit") private String positionReachedLimit;
    @ConfigField("npc-ranking.npc-help") private List<String> npcHelp;

    // commands

    @ConfigField("commands.incorrect-target") private String incorrectTarget;
    @ConfigField("commands.incorrect-usage") private String incorrectUsage;
    @ConfigField("commands.error") private String error;
    @ConfigField("commands.no-permission") private String noPermission;
    @ConfigField("commands.help-command") private List<String> helpCommand;
    @ConfigField("commands.help-command-staff") private List<String> helpCommandStaff;

    public static <T> T get(Function<MessageValue, T> function) {
        return function.apply(instance);
    }

}

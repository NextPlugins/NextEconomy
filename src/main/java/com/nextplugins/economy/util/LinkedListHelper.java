package com.nextplugins.economy.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nextplugins.economy.api.model.account.historic.AccountBankHistoric;

import java.lang.reflect.Type;
import java.util.LinkedList;

public final class LinkedListHelper {

    private static final Gson gson = new GsonBuilder().create();

    public static LinkedList<AccountBankHistoric> fromJson(String data) {

        Type typeOfHashMap = new TypeToken<LinkedList<AccountBankHistoric>>() {}.getType();
        return gson.fromJson(data, typeOfHashMap);

    }

    public static String toJson(LinkedList<AccountBankHistoric> data) {
        return gson.toJson(data);
    }

}

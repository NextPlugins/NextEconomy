package com.nextplugins.economy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class ListSerializerHelper<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public LinkedList<T> fromJson(String data) {

        Type typeOfHashMap = new TypeToken<LinkedList<T>>() {}.getType();
        return GSON.fromJson(data, typeOfHashMap);

    }

    public LinkedList<T> fromJson(FileReader reader) {

        Type typeOfHashMap = new TypeToken<LinkedList<T>>() {}.getType();
        return GSON.fromJson(reader, typeOfHashMap);

    }

    public String toJson(LinkedList<T> data) {
        return GSON.toJson(data);
    }

    public void toJson(LinkedList<T> data, FileWriter writer) {
        GSON.toJson(data, writer);
    }

}

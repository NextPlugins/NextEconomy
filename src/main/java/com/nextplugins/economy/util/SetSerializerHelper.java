package com.nextplugins.economy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Set;

public class SetSerializerHelper<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Set<T> fromJson(String data) {
        Type typeOfHashMap = new TypeToken<Set<T>>() {}.getType();
        return GSON.fromJson(data, typeOfHashMap);
    }

    public Set<T> fromJson(FileReader reader) {
        Type typeOfHashMap = new TypeToken<Set<T>>() {}.getType();
        return GSON.fromJson(reader, typeOfHashMap);
    }

    public String toJson(Set<T> data) {
        return GSON.toJson(data);
    }

    public void toJson(Set<T> data, FileWriter writer) {
        GSON.toJson(data, writer);
    }

}

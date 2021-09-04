package com.nextplugins.economy.ranking.util;

import lombok.Data;

import java.util.LinkedList;

@Data
public class RankingChatBody {

    private final LinkedList<String> bodyLines;

    public String[] asArray() {
        return bodyLines.toArray(new String[] {});
    }

}

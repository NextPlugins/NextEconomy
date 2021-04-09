package com.nextplugins.economy.util;

import java.util.LinkedHashMap;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class CaseInsensitiveLinkedMap<V> extends LinkedHashMap<String, V> {

    public static <V> CaseInsensitiveLinkedMap<V> newMap() {
        return new CaseInsensitiveLinkedMap<>();
    }

    @Override
    public V get(Object key) {
        String keyFounded = this.keySet()
                .stream()
                .filter(keyMap -> keyMap.equalsIgnoreCase((String) key))
                .findAny()
                .orElse(null);

        return keyFounded == null ? null : super.get(keyFounded);
    }

}

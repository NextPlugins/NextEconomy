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
        String keyFounded = null;
        for (String keyMap : this.keySet()) {
            if (keyMap.equalsIgnoreCase((String) key)) {
                keyFounded = keyMap;
                break;
            }
        }

        return keyFounded == null ? null : super.get(keyFounded);
    }

}

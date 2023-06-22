package com.nextplugins.economy.util;

import lombok.AllArgsConstructor;

/**
 * @author <a href="https://github.com/Yuhtin">Yuhtin</a>
 */
@AllArgsConstructor(staticName = "of")
public class Pair<K, T> {

    private final K key;
    private final T value;

    public K getLeft() { return key; }

    public T getRight() { return value; }

}

package com.b2z.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static <K, V> Map<K, V> map(Object... keyValues) {
        Map<K, V> map = new HashMap<>();

        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked")
            K key = (K) keyValues[i];

            @SuppressWarnings("unchecked")
            V value = (V) keyValues[i + 1];
            map.put(key, value);
        }
        return map;
    }


}

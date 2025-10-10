package com.b2z.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<Integer, Object> map(Object... keyValues) {
        Map<Integer, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            map.put((Integer) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }
    ;
}

package kim.ylem.heparser;

import kim.ylem.heparser.atoms.*;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class AtomMap  {
    public static TreeMap<String, Class<?>> map = new TreeMap<>(Comparator.comparingInt(String::length).reversed()
            .thenComparing(java.util.function.Function.identity()));

    static {
        Accent.register();
        Bold.register();
        Font.register();
        Fraction.register();
        Function.register();
        LargeOp.register();
        Limit.register();
        Matrix.register();
        Root.register();
        Symbol.register();
        UnderOver.register();
    }

    public static void put(String key, Class<?> value) {
        map.put(key, value);
        map.putIfAbsent(key.toUpperCase(), value);
        if (key.length() > 1) {
            map.putIfAbsent(Character.toUpperCase(key.charAt(0)) + key.substring(1), value);
        }
    }

    public static void remove(String key) {
        map.remove(key);
    }

    public static void putAll(Map<String, String> atomMap, Class<?> value) {
        atomMap.keySet().forEach(key -> put(key, value));
    }

    public static void putAllExact(Map<String, String> atomMap, Class<?> value) {
        atomMap.keySet().forEach(key -> map.put(key, value));
    }
}

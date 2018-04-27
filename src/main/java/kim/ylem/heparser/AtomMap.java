package kim.ylem.heparser;

import kim.ylem.heparser.atoms.*;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static java.util.function.Function.identity;

public class AtomMap  {
    // TODO: improve AtomMap, singleton?
    private AtomMap() {
    }

    private static final Map<String, AtomParser> map = new TreeMap<>(
            Comparator.comparingInt(String::length).reversed().thenComparing(identity()));

    static {
        AccentAtom.init();
        FontAtom.init();
        FractionAtom.init();
        FunctionAtom.init();
        Group.init();
        LeftRightAtom.init();
        MatrixAtom.init();
        OpAtom.init();
        RootAtom.init();
        SubSupAtom.init();
        TextAtom.init();
        UnderOverAtom.init();
    }

    public static AtomParser get(String key) {
        return map.get(key);
    }

    public static void put(String key, AtomParser value) {
        map.put(key, value);
        map.put(key.toUpperCase(), value);
        if (key.length() > 1) {
            map.put(Character.toUpperCase(key.charAt(0)) + key.substring(1), value);
        }
    }

    public static String search(String searchString) {
        if (map.containsKey(searchString)) {
            return searchString;
        }
        if (searchString.length() > 1) {
            for (String key : map.keySet()) {
                if (searchString.startsWith(key)) {
                    return key;
                }
            }
        }
        return null;
    }

    public static void remove(String key) {
        map.remove(key);
    }

    public static void putAll(Iterable<String> keySet, AtomParser value) {
        keySet.forEach(key -> put(key, value));
    }

    public static void putAllExact(Iterable<String> keySet, AtomParser value) {
        keySet.forEach(key -> map.put(key, value));
    }
}

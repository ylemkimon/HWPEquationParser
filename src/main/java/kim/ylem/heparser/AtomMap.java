package kim.ylem.heparser;

import kim.ylem.heparser.atoms.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class AtomMap  {
    private AtomMap() {
    }

    private static final Map<String, AtomParser> map = new HashMap<>(620);
    private static final Collection<String> special = new HashSet<>(75);
    private static final int MAX_LENGTH = 10;
    private static final int ASCII_UPPER_LOWER_OFFSET = 32;

    static {
        AccentAtom.init();
        FractionAtom.init();
        FunctionAtom.init();
        Group.init();
        LeftRightAtom.init();
        MatrixAtom.init();
        OpAtom.init();
        RootAtom.init();
        ScriptAtom.init();
        TextAtom.init();
        UnderOverAtom.init();
        SymbolMap.init();
    }

    public static AtomParser get(String key) {
        return map.get(key);
    }

    public static void register(AtomParser parser, String... commands) {
        for (String command : commands) {
            map.put(command, parser);
        }
    }

    public static void register(AtomParser parser, Collection<String> commands) {
        register(parser, commands, false);
    }

    public static void register(AtomParser parser, Collection<String> commands, boolean isSpecial) {
        for (String key : commands) {
            map.put(key, parser);
        }
        if (isSpecial) {
            special.addAll(commands);
        }
    }

    private static boolean isASCIIUpperCase(char c) {
        return c <= 'Z' && c >= 'A';
    }

    private static char toASCIILowerCase(char c) {
        return isASCIIUpperCase(c) ? (char) (c + ASCII_UPPER_LOWER_OFFSET) : c;
    }

    public static String search(String s) {
        if (map.containsKey(s)) {
            return s;
        }
        if (s.length() <= 1) {
            return null;
        }
        for (int i = 2; i <= 4 && i < s.length(); i++) {
            String sub = s.substring(0, i);
            if (special.contains(sub)) {
                return sub;
            }
        }

        int style = isASCIIUpperCase(s.charAt(0)) ? (isASCIIUpperCase(s.charAt(1)) ? 2 : 1) : 0;
        char[] search = new char[MAX_LENGTH];
        search[0] = toASCIILowerCase(s.charAt(0));
        int length;
        for (length = 1; length < s.length() && length < MAX_LENGTH; length++) {
            if ((style == 2) != isASCIIUpperCase(s.charAt(length))) {
                break;
            }
            search[length] = toASCIILowerCase(s.charAt(length));
        }

        for (int i = length; i > 1; i--) {
            String sub = new String(search, 0, i);
            if (map.containsKey(sub)) {
                if (style != 0) {
                    if (special.contains(sub)) {
                        continue;
                    }

                    search[0] -= ASCII_UPPER_LOWER_OFFSET;
                    String camel = new String(search, 0, i);
                    if (map.containsKey(camel)) {
                        return camel;
                    }
                }
                return sub;
            }
        }
        return null;
    }

    /**
     * Following commands should be added to special:
     *
     * <p>1. a command that has 2 to 4 letters and (i) takes precedence over other long
     * commands that start with its name or (ii) contains upper case(s) but doesn't have
     * lower case counterpart
     *
     * <p>2. a command that has more than 4 letters and only lower case form should be
     * allowed
     * @param key name of the special command
     */
    public static void addSpecial(String key) {
        special.add(key);
    }
}

package kim.ylem.heparser;

import kim.ylem.heparser.atoms.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class AtomMap {
    private static final Map<String, AtomParser> map = new HashMap<>(620);
    private static final Collection<String> special = new HashSet<>(75);

    static {
        SymbolMap.init();

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
    }

    private AtomMap() {
    }

    @Nullable
    @Contract(pure = true)
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
        for (String command : commands) {
            map.put(command, parser);
        }
        if (isSpecial) {
            special.addAll(commands);
        }
    }

    static boolean containsKey(String key) {
        return map.containsKey(key);
    }

    static boolean isSpecial(String key) {
        return special.contains(key);
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
     *
     * @param key name of the special command
     */
    public static void addSpecial(String key) {
        special.add(key);
    }
}

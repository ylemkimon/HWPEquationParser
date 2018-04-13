package kim.ylem.heparser.atoms;

import kim.ylem.heparser.AtomMap;

import java.util.HashMap;

public class Limit extends Atom {
    private static final HashMap<String, String> limitMap = new HashMap<>();

    public static void register() {
        limitMap.put("lim", "\\lim");
        limitMap.put("Lim", "\\operatorname*{Lim}");

        AtomMap.putAllExact(limitMap, Limit.class);
    }

    private final String function;
    private final Group sub;

    public Limit(String function, Group sub) {
        this.function = limitMap.get(function);
        this.sub = sub;
    }

    @Override
    protected String toLaTeX(int flag) {
        String result = function;
        if (sub != null) {
            result += "_{" + sub.toLaTeX(flag) + "}";
        }
        return result + " ";
    }
}

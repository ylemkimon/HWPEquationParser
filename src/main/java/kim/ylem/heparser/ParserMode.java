package kim.ylem.heparser;

import kim.ylem.heparser.atoms.ScriptAtom;

public enum ParserMode {
    ARGUMENT(0),
    ARGUMENT_1(ARGUMENT, 1),
    ARGUMENT_2(ARGUMENT, 2),
    ARGUMENT_3(ARGUMENT, 3),
    ARGUMENT_4(ARGUMENT, 4),
    ARGUMENT_5(ARGUMENT, 5),
    GROUP(9),
    TERM(9),
    SUB_TERM(TERM, 9, ScriptAtom.Mode.IN_SUB),
    UNDEROVER_TERM(TERM, 9, ScriptAtom.Mode.UNDEROVER),
    SYMBOL(1),
    DELIMITER(1);

    public static ParserMode getArgumentMode(int maxLength) {
        for (ParserMode mode : ParserMode.values()) {
            if (mode.parserMode == ARGUMENT && mode.maxLength == maxLength) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid command length!");
    }

    private final ParserMode parserMode;
    private final int maxLength;
    private final ScriptAtom.Mode scriptParseMode;

    ParserMode(int maxLength) {
        this(null, maxLength);
    }

    ParserMode(ParserMode parserMode, int maxLength) {
        this(parserMode, maxLength, ScriptAtom.Mode.NORMAL);
    }

    ParserMode(ParserMode parserMode, int maxLength, ScriptAtom.Mode scriptParseMode) {
        this.parserMode = parserMode;
        this.maxLength = maxLength;
        this.scriptParseMode = scriptParseMode;
    }

    public ParserMode getParserMode() {
        return parserMode;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public ScriptAtom.Mode getScriptParseMode() {
        return scriptParseMode;
    }
}

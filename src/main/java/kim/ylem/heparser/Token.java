package kim.ylem.heparser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

class Token {
    private final String text;
    private final AtomParser atomParser;
    private final int length;

    Token(String text, boolean isCommand) {
        this(text, isCommand, false);
    }

    Token(String text, boolean isCommand, boolean forceSymbol) {
        atomParser = isCommand && !forceSymbol ? AtomMap.get(text) : null;
        this.text = isCommand && atomParser == null ? SymbolMap.getSymbol(text) : text;
        length = text.length();
    }

    @Contract(pure = true)
    int getLength() {
        return length;
    }

    @Contract(pure = true)
    @Nullable
    AtomParser getAtomParser() {
        return atomParser;
    }

    @Override
    public String toString() {
        return text;
    }
}

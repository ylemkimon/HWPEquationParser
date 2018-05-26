package kim.ylem.heparser;

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

    int getLength() {
        return length;
    }

    AtomParser getAtomParser() {
        return atomParser;
    }

    @Override
    public String toString() {
        return text;
    }
}

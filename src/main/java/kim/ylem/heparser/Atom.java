package kim.ylem.heparser;

public interface Atom {
    default boolean isFromToAllowed() {
        return false;
    }
}

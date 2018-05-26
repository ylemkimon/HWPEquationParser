package kim.ylem.heparser;

import java.io.Serializable;

public interface Atom extends Serializable {
    default boolean isFromToAllowed() {
        return false;
    }
}

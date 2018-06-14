package kim.ylem.hmlparser;

@FunctionalInterface
public interface Updatable {
    void update(String placeholder, String replacement);
}

package dansapps.interakt.actions.abs;

public interface Action {
    String name = "move";

    default String getName() {
        return name;
    }
}
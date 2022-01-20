package dansapps.interakt.actions.abs;

/**
 * @author Daniel McCoy Stephenson
 * @since January 17th, 2022
 */
public interface Action {
    String name = "move";

    default String getName() {
        return name;
    }
}
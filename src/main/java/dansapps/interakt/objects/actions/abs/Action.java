package dansapps.interakt.objects.actions.abs;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public abstract class Action {
    private String name;

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
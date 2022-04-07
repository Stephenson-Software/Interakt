package dansapps.interakt.misc;

/**
 * @author Daniel McCoy Stephenson
 * @since January 15th, 2022
 */
public class CONFIG {
    public static boolean DEBUG_FLAG = false;
    public static final int TIME_SLOT_LENGTH_IN_SECONDS = 16;
    public static final int GRID_SIZE = 10;
    public static final int MAX_CHANCE_TO_MOVE = 25;
    public static final int MAX_CHANCE_TO_BEFRIEND = 50;
    public static final int MAX_CHANCE_TO_ATTACK = 50;
    public static final int MAX_CHANCE_TO_REPRODUCE = 10;
    public static final int SECONDS_BETWEEN_AUTO_SAVES = 300;
    public static final double MAX_HEALTH = 100;
    public static final double MAX_DAMAGE = 50;
    public static final GRID_DISPLAY_TYPE DISPLAY_TYPE = GRID_DISPLAY_TYPE.NUMBER_OF_ENTITIES;
    public static final boolean SHOW_LINEAGE_INFO = true;
    public static final boolean LOG_EVENTS_TO_CONSOLE = true;
}
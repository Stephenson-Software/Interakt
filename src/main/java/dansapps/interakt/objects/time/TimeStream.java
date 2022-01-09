package dansapps.interakt.objects.time;

import java.util.ArrayList;

/**
 * @author Daniel McCoy Stephenson
 * @since January 8th, 2022
 * @brief This class is intended to represent a stream of time slots.
 */
public class TimeStream {
    private ArrayList<TimeSlot> timeSlots = new ArrayList<>();

    public TimeStream() {

    }

    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public void addTimeSlot(TimeSlot timeslot) {
        timeSlots.add(timeslot);
    }

    public TimeSlot getMostRecentTimeSlot() {
        return timeSlots.get(timeSlots.size() - 1);
    }
}
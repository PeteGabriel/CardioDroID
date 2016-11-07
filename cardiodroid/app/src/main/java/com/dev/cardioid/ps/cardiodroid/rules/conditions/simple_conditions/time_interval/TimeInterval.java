package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions.time_interval;

/**
 * TODO
 *
 * @ThreadSafe
 */
public final class TimeInterval {

    private Time start;
    private Time end;

    /**
     * If a given parameter is given as null, we place the maximum or minimum of the interval
     * in it's place.
     *
     * Minimum -> start = 00:00:00
     * Maximum -> end = 23:59:59
     * */

    public TimeInterval(Time _start, Time _end) {
        start = _start == null?
                new Time(0, 0, 0) : _start;

        end = _end == null?
                new Time(23, 59, 59) : _end;
    }

    public TimeInterval(String _start, String _end) {
        start = _start == null?
                new Time(0, 0, 0) : new Time(_start);

        end = _end == null?
                new Time(23, 59, 59) : new Time(_end);
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }
}

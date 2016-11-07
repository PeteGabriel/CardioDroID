package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions.time_interval;

import java.security.InvalidParameterException;

/**
 * TODO
 *
 * @ThreadSafe
 */
public final class Time {

  private int hour;
  private int minute;
  private int second;

  /**
   * Receives the Time in the following format:
   * HH:MM:SS
   **/
  public Time(String time) {
    String[] timeParts = time.split(":");

    if (timeParts.length != 3) {
      throw new InvalidParameterException(
          "The time has to be formatted int he following way: 'HH:MM:SS'.");
    }

    hour = Integer.parseInt(timeParts[0]);
    if (hour >= 24) {
      throw new InvalidParameterException("The hour (HH) has to be between '0' and '23'.");
    }

    minute = Integer.parseInt(timeParts[1]);
    if (minute >= 60 || minute < 0) {
      throw new InvalidParameterException("The minute (MM) have to be between '0' and '59'.");
    }

    second = Integer.parseInt(timeParts[2]);
    if (second >= 60 || second < 0) {
      throw new InvalidParameterException("The second (SS) have to be between '0' and '59'.");
    }
  }

  public Time(int _hour, int _minute, int _second) {
    if (_hour >= 24) {
      throw new InvalidParameterException("The hour (HH) has to be between '0' and '23'.");
    }

    if (_minute >= 60 || _minute < 0) {
      throw new InvalidParameterException("The minute (MM) have to be between '0' and '59'.");
    }

    if (_second >= 60 || _second < 0) {
      throw new InvalidParameterException("The second (SS) have to be between '0' and '59'.");
    }

    hour = _hour;
    minute = _minute;
    second = _second;
  }

  @Override public String toString() {
    return String.format("%s:%s:%s", Integer.toString(hour), Integer.toString(minute),
        Integer.toString(second));
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public int getSecond() {
    return second;
  }
}

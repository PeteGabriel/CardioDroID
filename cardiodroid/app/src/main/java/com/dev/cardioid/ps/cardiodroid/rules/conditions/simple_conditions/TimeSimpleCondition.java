package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions;

import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions.time_interval.TimeInterval;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonTimeIntervalModel;

import org.joda.time.DateTime;

import java.security.InvalidParameterException;
import java.util.Map;

/**
 * This SimpleCondition represents all the possible Time related context conditions.
 *
 * The types of conditions supported are:
 * Greater Than a given time;
 * Less Than a given Time;
 * In a given interval of Time.
 *
 * The fixed value type is TimeInterval, which can hold two values: start and end.
 * This same type is used for all evaluation types.
 **/
public class TimeSimpleCondition extends SimpleConditionAbstract<DateTime, TimeInterval> {


  /**
   * This constructor allows for the start and end times fo the ineterval to be defined in a Map.
   *
   * @param fixedValueParams the values needed to instantiate a TimeInterval.
   * @param evaluator the IEvaluator to be used when it comes time to evaluate the SimpleCondition
   */
  public TimeSimpleCondition(Map<String, Object> fixedValueParams, IEvaluator evaluator)
      throws InvalidParameterException {
    super();

    String startTime = (String) fixedValueParams.get(JsonTimeIntervalModel.TIME_INTERVAL_START);
    String endTime = (String) fixedValueParams.get(JsonTimeIntervalModel.TIME_INTERVAL_END);

    this.fixedValue = new TimeInterval(startTime, endTime);
    this.evaluator = evaluator;
  }

  /**
   * Obtain the Current Time.
   *
   * Makes use of the java.util.Calendar.
   */
  @Override
  protected DateTime getCurrentValue() {
    return new DateTime();
  }

  /**
   * This evaluation method checks if the current time is greater than the fixedValue.
   *
   * Being that the fixedValue is an instance of TimeInterval, it can have two times.
   * The start time of this interval is what is used for comparison.
   *
   * @return returns the boolean value indicating if the current time is after the time
   * specified by fixedValue.start.
   */
  @Override
  public boolean greaterThan(DateTime now) {
    return checkGreaterThan(now);
  }

  private boolean checkGreaterThan(DateTime now){
    if(now.getHourOfDay() < fixedValue.getStart().getHour()) return false;
    else if(now.getHourOfDay() == fixedValue.getStart().getHour()
            && now.getMinuteOfDay() < fixedValue.getStart().getMinute()) return false;
    else if(now.getHourOfDay() == fixedValue.getStart().getHour()
            && now.getMinuteOfDay() == fixedValue.getStart().getMinute()
            && now.getSecondOfDay() < fixedValue.getStart().getSecond()) return false;

    return true;
  }

  @Override public boolean equalsFixed(DateTime dateTime) {
    return in(dateTime);
  }

  /**
   * This evaluation method checks if the current time is less than the fixedValue.
   *
   * Being that the fixedValue is an instance of TimeInterval, it can have two times.
   * The end time of this interval is what is used for comparison.
   *
   * @return returns the boolean value indicating if the current time is before the time
   * specified by fixedValue.end.
   */
  @Override
  public boolean lessThan(DateTime now) {
    return checkLessThan(now);
  }


  private boolean checkLessThan(DateTime now){
    if(now.getHourOfDay() > fixedValue.getEnd().getHour()) return false;
    else if(now.getHourOfDay() == fixedValue.getEnd().getHour()
            && now.getMinuteOfDay() > fixedValue.getEnd().getMinute()) return false;
    else if(now.getHourOfDay() == fixedValue.getEnd().getHour()
            && now.getMinuteOfDay() == fixedValue.getEnd().getMinute()
            && now.getSecondOfDay() > fixedValue.getEnd().getSecond()) return false;

    return true;
  }

  /**
   * This evaluation method checks if the current time is in the fixedValue.
   *
   * Being that the fixedValue is an instance of TimeInterval, it can have two times,
   * times these which establish an interval which the current value should be in.
   *
   * @return returns the boolean value indicating if the the current time is in the interval
   * specified by fixedValue.
   */
  @Override
  public boolean in(DateTime now) {
    return checkGreaterThan(now) && checkLessThan(now);
  }


}

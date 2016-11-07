package com.dev.cardioid.ps.cardiodroid.rules;

import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

/**
 * The IEvaluator serves as a redirection to one of the evaluation methods of a SimpleCondition.
 */
public interface IEvaluator {
  /**
   * The evaluate method is generic in T.
   *
   * @param condition the instance of SimpleCondition which will be used to obtain the evaluation.
   * @param t the current value which will be passed to the evaluation method of the
   * SimpleCondition.
   * @return return the result of the evaluation by the SimpleCondition.
   */
  <T> boolean evaluate(SimpleConditionAbstract condition, T t);

  class Type {
    public static final String GREATER_THAN = "GREATER-THAN";
    public static final String LESS_THAN = "LESS-THAN";
    public static final String EQUALS = "EQUALS";
    public static final String IN = "IN";
  }
}

package com.dev.cardioid.ps.cardiodroid.rules.conditions;

import com.dev.cardioid.ps.cardiodroid.rules.ICondition;

/**
 * A ComposedCondition is itself a Condition which is composed of multiple Conditions
 * and the Relations established between them.
 */
public abstract class ComposedConditionAbstract implements ICondition {


  public static final String IDENTIFIER = "COMPOSED";

  protected ICondition condition1;
  protected ICondition condition2;

  public ComposedConditionAbstract(ICondition condition1, ICondition condition2) {
    this.condition1 = condition1;
    this.condition2 = condition2;
  }

  /**
   * Evaluation of a ComposedCondition consists of evaluating a pair of conditions and ....
   */
  public abstract boolean evaluate();

  public static class Type {
    public static final String AND_CONDITION = "AND";
    public static final String OR_CONDITION = "OR";
  }
}

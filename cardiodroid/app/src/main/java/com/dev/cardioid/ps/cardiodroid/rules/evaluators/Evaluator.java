package com.dev.cardioid.ps.cardiodroid.rules.evaluators;

import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;

/**
 * Used to create instances
 * of a {@link InEvaluator} concrete
 * implementation.
 */
public final class Evaluator {

  public static IEvaluator createEvaluator(String type) {
    switch (type) {
      case IEvaluator.Type.IN:
        return new InEvaluator();
      case IEvaluator.Type.EQUALS:
        return new EqualsEvaluator();
      case IEvaluator.Type.GREATER_THAN:
        return new GreaterThanEvaluator();
      case IEvaluator.Type.LESS_THAN:
        return new LessThanEvaluator();
      default:
        return null;
    }
  }
}

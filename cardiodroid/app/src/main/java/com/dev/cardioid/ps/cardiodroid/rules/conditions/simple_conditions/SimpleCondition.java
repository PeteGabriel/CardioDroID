package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions;

import android.content.Context;
import com.dev.cardioid.ps.cardiodroid.contexts.ContextEnvironment;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

import java.util.Map;

/**
 * Used to create instances
 * of a {@link SimpleConditionAbstract} concrete
 * implementation.
 */
public final class SimpleCondition {

  public static SimpleConditionAbstract createSimpleCondition(Context ctx, String type,
      Map<String, Object> fixedValueParams, IEvaluator evaluator){
    switch (type){
      case ContextEnvironment.Types.EXHAUSTION:
        return new ExhaustionSimpleCondition(ctx, fixedValueParams, evaluator);
      case ContextEnvironment.Types.LOCATION:
        return new LocationSimpleCondition(ctx, fixedValueParams, evaluator);
      case ContextEnvironment.Types.TIME:
        return new TimeSimpleCondition(fixedValueParams, evaluator);
      case ContextEnvironment.Types.WEATHER:
        return new WeatherSimpleCondition(ctx, fixedValueParams, evaluator);
      default:
        return null;
    }
  }

}

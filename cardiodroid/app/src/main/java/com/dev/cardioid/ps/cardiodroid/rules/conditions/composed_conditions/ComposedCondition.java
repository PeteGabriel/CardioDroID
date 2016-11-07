package com.dev.cardioid.ps.cardiodroid.rules.conditions.composed_conditions;

import com.dev.cardioid.ps.cardiodroid.rules.ICondition;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;

/**
 *Used to create instances
 * of a {@link ComposedConditionAbstract} concrete
 * implementation.
 */
public final class ComposedCondition {

  public static ComposedConditionAbstract createComposedCondition(String type, ICondition one, ICondition two){
    switch (type){
      case ComposedConditionAbstract.Type.AND_CONDITION:
        return new AndComposedCondition(one, two);
      case ComposedConditionAbstract.Type.OR_CONDITION:
        return new OrComposedCondition(one, two);
      default:
        return null;
    }
  }
}

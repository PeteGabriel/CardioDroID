package com.dev.cardioid.ps.cardiodroid.rules.evaluators;

import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

public class GreaterThanEvaluator implements IEvaluator {
    @Override
    public <T> boolean evaluate(SimpleConditionAbstract condition, T t) {
        return condition.greaterThan(t);
    }

    @Override public String toString() {
        return "GREATER_THAN";
    }
}

package com.dev.cardioid.ps.cardiodroid.rules.evaluators;

import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

public class LessThanEvaluator implements IEvaluator {

    @Override
    public <T> boolean evaluate(SimpleConditionAbstract condition, T t) {
        return condition.lessThan(t);
    }

    @Override public String toString() {
        return "LESS_THAN";
    }
}

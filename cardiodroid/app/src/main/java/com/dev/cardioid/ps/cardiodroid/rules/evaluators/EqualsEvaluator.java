package com.dev.cardioid.ps.cardiodroid.rules.evaluators;

import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;

public class EqualsEvaluator implements IEvaluator {
    @Override
    public <T> boolean evaluate(SimpleConditionAbstract condition, T t) {
        return condition.equalsFixed(t);
    }

    @Override public String toString() {
        return "EQUALS";
    }
}

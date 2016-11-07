package com.dev.cardioid.ps.cardiodroid.rules.conditions.composed_conditions;

import com.dev.cardioid.ps.cardiodroid.rules.ICondition;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;

public class OrComposedCondition extends ComposedConditionAbstract {


    public OrComposedCondition(ICondition condition1, ICondition condition2) {
        super(condition1, condition2);
    }

    @Override
    public boolean evaluate() {
        return this.condition1.evaluate()
                || this.condition2.evaluate();
    }


}

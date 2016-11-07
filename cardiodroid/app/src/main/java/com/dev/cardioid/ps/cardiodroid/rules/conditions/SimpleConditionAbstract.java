package com.dev.cardioid.ps.cardiodroid.rules.conditions;

import com.dev.cardioid.ps.cardiodroid.rules.ICondition;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;

public abstract class SimpleConditionAbstract<T, V> implements ICondition {

    public static final String IDENTIFIER = "SIMPLE";

    protected IEvaluator evaluator;

    /**
     * This value holds the constant operand value.
     * V is a context relevant Type.
     * */
    protected V fixedValue;

    /**
     * Obtain the current value for a given context.
     *
     * @return T returns an Object of type T which is relevant to the context.
     * */
    protected abstract T getCurrentValue();

    public SimpleConditionAbstract() {
    }

  @Override public String toString() {
    return "(SimpleCondition: Value-> " + fixedValue + " Evaluator-> " + evaluator.toString() + ")";
  }

  /**
     * @param fixedValue The value which is to be used for comparison for a given context.
     * @param eval The Evaluator which is to be used for the comparison.
     * */
    public SimpleConditionAbstract(V fixedValue, IEvaluator eval) {
        this.fixedValue = fixedValue;
        this.evaluator = eval;
    }

    public final boolean evaluate(){
        return evaluate(getCurrentValue());
    }

    public final boolean evaluate(T t){
        return t != null && evaluator.evaluate(this, t);

    }

    /***************************************************************************
     * Each SimpleCondition implementation must implement the evaluation methods they support.
     ***************************************************************************/

    public boolean greaterThan(T t){
        throw new UnsupportedOperationException("The greaterThan evaluator is not supported.");
    }

    public boolean lessThan(T t){
        throw new UnsupportedOperationException("The lessThan evaluator is not supported.");
    }


    public boolean equalsFixed(T t){
        throw new UnsupportedOperationException("The equals evaluator is not supported.");
    }

    public boolean in(T t){
        throw new UnsupportedOperationException("The in evaluator is not supported.");
    }

}
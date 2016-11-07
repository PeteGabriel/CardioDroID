package com.dev.cardioid.ps.cardiodroid.rules;

/**
 * A ICondition is a Functional Interface which Represents a condition
 * which can be evaluated in the future.
 * */
public interface ICondition {
    /**
     * This method evaluates the Condition represented by this Object.
     * @return The boolean value which results in the evaluation of the condition.
     * */
    boolean evaluate();

}

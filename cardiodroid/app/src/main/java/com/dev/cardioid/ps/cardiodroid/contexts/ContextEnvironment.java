package com.dev.cardioid.ps.cardiodroid.contexts;

/**
 * TODO
 */

public final class ContextEnvironment {

    private ContextEnvironment(){}

    public static class Types{
        public static final String EXHAUSTION = "EXHAUSTION";
        public static final String TIME = "TIME";
        public static final String WEATHER = "WEATHER";
        public static final String LOCATION = "LOCATION";
    }

    public static class ExhaustionStates{
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
    }

}

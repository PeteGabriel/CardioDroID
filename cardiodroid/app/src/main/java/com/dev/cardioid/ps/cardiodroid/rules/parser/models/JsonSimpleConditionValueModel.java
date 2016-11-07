package com.dev.cardioid.ps.cardiodroid.rules.parser.models;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonSimpleConditionValueModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String SIMPLE_CONDITION_VALUE_CONTEXT = "context";
    public static final String SIMPLE_CONDITION_VALUE_EVALUATOR = "evaluator";
    public static final String SIMPLE_CONDITION_VALUE_FIXED_VALUE = "fixed_value";

    /**
     * Method to create a serialized version of this JSON object.
     **/

    public static JSONObject create(String type, String evaluator, JSONObject fixedValue) {
        try {
            JSONObject obj = new JSONObject();

            obj.put(SIMPLE_CONDITION_VALUE_CONTEXT, type);
            obj.put(SIMPLE_CONDITION_VALUE_EVALUATOR, evaluator);
            obj.put(SIMPLE_CONDITION_VALUE_FIXED_VALUE, fixedValue);

            return obj;
        }catch(JSONException e){
            // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
            return new JSONObject();
        }
    }
}

package com.dev.cardioid.ps.cardiodroid.rules.parser.models;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonComposedConditionValueModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String COMPOSED_CONDITION_CONDITION_1 = "condition1";
    public static final String COMPOSED_CONDITION_CONDITION_2 = "condition2";
    public static final String COMPOSED_CONDITION_RELATION = "relation";

    /**
     * Method to create a serialized version of this JSON object.
     **/
    public static JSONObject create(JSONObject condition1, JSONObject condition2, String relation) {
        try {
            JSONObject obj = new JSONObject();

            obj.put(COMPOSED_CONDITION_CONDITION_1, condition1);
            obj.put(COMPOSED_CONDITION_CONDITION_2, condition2);
            obj.put(COMPOSED_CONDITION_RELATION, relation);

            return obj;
        }catch(JSONException e){
            // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
            return new JSONObject();
        }
    }
}

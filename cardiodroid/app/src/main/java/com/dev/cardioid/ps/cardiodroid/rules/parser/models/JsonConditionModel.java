package com.dev.cardioid.ps.cardiodroid.rules.parser.models;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonConditionModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String CONDITION_TYPE = "type";
    public static final String CONDITION_VALUE = "value";

    /**
     * Definition of the Condition Types
     **/

    public static final String CONDITION_TYPE_SIMPLE = "SIMPLE";
    public static final String CONDITION_TYPE_COMPOSED = "COMPOSED";

    /**
     * Method to create a serialized version of this JSON object.
     **/
    public static JSONObject create(String type, JSONObject value){
        try {
            JSONObject obj = new JSONObject();

            obj.put(CONDITION_TYPE, type);
            obj.put(CONDITION_VALUE, value);

            return obj;
        }catch(JSONException e){
            return null;
        }
    }
}

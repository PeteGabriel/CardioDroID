package com.dev.cardioid.ps.cardiodroid.rules.parser.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonRuleModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String RULE_CONDITION = "condition";
    public static final String RULE_ACTIONS = "actions";
    public static final String RULE = "rule";
    public static final String RULE_NAME = "name";
    public static final String RULE_TYPE = "type";

    /**
     * Method to create a serialized version of this JSON object.
     **/

    public static JSONObject create(JSONObject condtition, JSONArray actions) {
        try {
            JSONObject obj = new JSONObject();

            obj.put(RULE_CONDITION, condtition);
            obj.put(RULE_ACTIONS, actions);

            return obj;
        }catch(JSONException e){
            // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
            return new JSONObject();
        }
    }

    public static JSONArray createActionArray(Iterator<String> actions){
        JSONArray jsonArr = new JSONArray();
        while(actions.hasNext())
            jsonArr.put(actions.next());

        return jsonArr;
    }

    public static JSONObject emptyObject(){
        return new JSONObject();
    }
}

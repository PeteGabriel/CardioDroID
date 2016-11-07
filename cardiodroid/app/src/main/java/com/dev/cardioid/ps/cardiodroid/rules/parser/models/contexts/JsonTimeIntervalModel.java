package com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonTimeIntervalModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String TIME_INTERVAL_START = "start_time";
    public static final String TIME_INTERVAL_END = "end_time";

    /**
     * Method to create a serialized version of this JSON object.
     **/

    public static JSONObject create(String start_time, String end_time) {

        try {
            JSONObject obj = new JSONObject();

            obj.put(TIME_INTERVAL_START, start_time);
            obj.put(TIME_INTERVAL_END, end_time);

            return obj;
        }catch(JSONException e){
            Log.e("JsonTimeIntervalModel", e.getMessage());
            return new JSONObject();
        }
    }
}

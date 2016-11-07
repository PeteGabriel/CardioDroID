package com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonWeatherModel {

    /**
     * Definition of the Properties of the Condition JSON object
     **/

    public static final String WEATHER_CONDITION_VALUE = "value";

    /**
     * Method to create a serialized version of this JSON object.
     **/

    public static JSONObject create(String weather_condition) {
        // TODO check if the supplied weather condition is a valid one,
        // refer to the available values of the WeatherContextManager.
        try {
            JSONObject obj = new JSONObject();

            obj.put(WEATHER_CONDITION_VALUE, weather_condition);

            return obj;
        } catch (JSONException e) {
            // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
            return new JSONObject();
        }
    }
}

package com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonExhaustionModel {

  /**
   * Definition of the Properties of the Condition JSON object
   **/

  public static final String EXHAUSTION_LEVEL = "level";

  /**
   * Method to create a serialized version of this JSON object.
   **/

  public static JSONObject create(String exhaustion_level) {

    try {
      JSONObject obj = new JSONObject();

      obj.put(EXHAUSTION_LEVEL, exhaustion_level);

      return obj;
    }catch(JSONException e){
      // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
      return new JSONObject();
    }
  }
}

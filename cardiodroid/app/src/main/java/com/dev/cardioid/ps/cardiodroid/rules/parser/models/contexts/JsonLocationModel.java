package com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLocationModel {
  /**
   * Definition of the Properties of the Condition JSON object
   **/

  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String RADIUS = "radius";
  public static final String REQUEST_ID = "request_id";

  /**
   * Method to create a serialized version of this JSON object.
   **/
  public static JSONObject create(double latitude, double longitude, double radius, String requestId) {
    try {
      JSONObject obj = new JSONObject();

      obj.put(LATITUDE, latitude);
      obj.put(LONGITUDE, longitude);
      obj.put(RADIUS, radius);
      obj.put(REQUEST_ID, requestId);

      return obj;
    }catch(JSONException e){
      // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
      return new JSONObject();
    }
  }
}

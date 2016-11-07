package com.dev.cardioid.ps.cardiodroid.rules.parser.models;

import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonNamedRuleModel {

  /**
   * Definition of the Properties of the Condition JSON object
   **/

  public static final String RULE_NAME = "name";
  public static final String RULE_OBJECT = "rule";

  /**
   * Method to create a serialized version of this JSON object.
   **/
  public static JSONObject create(String name, JSONObject rule, List<String> contexts) {
    try {
      JSONObject obj = new JSONObject();

      obj.put(RULE_NAME, name);
      obj.put(RULE_OBJECT, rule);
      obj.getJSONObject(RULE_OBJECT).put(JsonRuleContext.CONTEXTS, new JSONArray(contexts));

      return obj;
    }catch(JSONException e){
      // No reason for this exception to be thrown, but if it is, return an empty JSONObject.
      return new JSONObject();
    }
  }
}

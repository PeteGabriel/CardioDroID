package com.dev.cardioid.ps.cardiodroid.rules.parser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.rules.IAction;
import com.dev.cardioid.ps.cardiodroid.rules.ICondition;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.rules.actions.Action;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.ComposedConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.composed_conditions.ComposedCondition;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions.SimpleCondition;
import com.dev.cardioid.ps.cardiodroid.rules.evaluators.Evaluator;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonComposedConditionValueModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonConditionModel;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonRuleModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonRulesParser {
    /**
     * Create a Rule from a serialized JSON object.
     *
     * @param jsonStr The Rule in JSON format.
     * @return An instance of a Rule which represents the rule established by the JSON object.
     * */
    @Nullable
    public static Rule parseRule(Context context, String jsonStr) throws JsonRuleException {
      try {
        JSONObject jsonRule = new JSONObject(jsonStr);
        JSONObject tmp =  jsonRule.has(JsonRuleContext.RULE_OBJECT) ?
            jsonRule.getJSONObject(JsonRuleContext.RULE_OBJECT) : jsonRule;
        JSONObject conditionJsonObj = tmp.getJSONObject(JsonRuleModel.RULE_CONDITION);
        ICondition condition = parseCondition(context, conditionJsonObj);
        List<String> ctxs = parseArray(tmp.getJSONArray(JsonRuleContext.CONTEXTS));
        JSONArray actionsArr = tmp.getJSONArray(JsonRuleModel.RULE_ACTIONS);
        return new Rule(condition, parseAction(context, actionsArr), jsonRule, ctxs);
      } catch (JSONException e) {
        throw new JsonRuleException(e.getMessage());
      }
    }

    public static Rule parseRuleNoActions(Context context, String jsonStr) throws JsonRuleException {
        try {
          JSONObject jsonRule = new JSONObject(jsonStr);
          JSONObject tmp =  jsonRule.has(JsonRuleContext.RULE_OBJECT) ?
              jsonRule.getJSONObject(JsonRuleContext.RULE_OBJECT) : jsonRule;
          JSONObject conditionJsonObj = tmp.getJSONObject(JsonRuleModel.RULE_CONDITION);
          ICondition condition = parseCondition(context, conditionJsonObj);
          List<String> ctxs = parseArray(tmp.getJSONArray(JsonRuleContext.CONTEXTS));
          return new Rule(condition, null, jsonRule, ctxs);
        } catch (JSONException e) {
            throw new JsonRuleException("Badly formed JSON Rule object");
        }
    }


    /**
     * Parse a given Condition from a JSONObject
     *
     * @param jsonCondition the object from which we are to extract an Create a ICondition.
     * @return Intance of the ICondition object which corresponds to the condition represented by the JSONObject.
     * @throws JsonRuleException when one of the values which we tried to extract from the JSONObject is not found.
     * */
    @Nullable public static ICondition parseCondition(Context context, JSONObject jsonCondition) throws JsonRuleException{
        try {
            String conditionType = jsonCondition.getString(JsonConditionModel.CONDITION_TYPE);

            if (conditionType.equals(SimpleConditionAbstract.IDENTIFIER)) {
                JSONObject valueFromCondition = jsonCondition.getJSONObject(JsonConditionModel.CONDITION_VALUE);
                ICondition cond = parseSimpleCondition(context, valueFromCondition);
                return cond;
            }else if (conditionType.equals(ComposedConditionAbstract.IDENTIFIER))
                return parseComposedCondition(context, jsonCondition.getJSONObject(JsonConditionModel.CONDITION_VALUE));
            else
                throw new JsonRuleException("An invalid condition value type was specified: " + conditionType);
        }catch(JSONException e){
            // If this Exception is caught, it is because one of the values which we tried
            // to extract from the JSONObject was not found.
            throw new JsonRuleException("Badly formed JSON condition object.");
        }
    }

    /**
     * Parse an array of actions, which represent IActions, into a Collection<IAction>
     *
     * In order to Achieve this, we have to check if the specified action is in the Map of
     * Actions (actionMap), and if so then we can obtain the value associated to this key and
     * instantiate the corresponding IAction object.
     *
     * @param jsonActions the JSON array which contains the actions to be parsed.
     * @return Collection of IAction objects.
     * */
    @NonNull
    private static Collection<IAction> parseAction(Context ctx, JSONArray jsonActions) throws JsonRuleException{
      ArrayList<IAction> actions = new ArrayList<>();
      try {
        for (int i = 0; i < jsonActions.length(); ++i) {
          String actionKey = jsonActions.getString(i);
          IAction anAction = Action.createAction(ctx, actionKey);
          if (anAction == null) {
            throw new JsonRuleException(
                String.format("The specified action (%s) is not valid.", actionKey));
          }
          actions.add(anAction);
        }
        return actions;
      }catch(JSONException e){
        throw new JsonRuleException("Problem obtaining Action form JSONArray.");
      }
    }

    /**
     * Parses a JSONArray into a list of strings. It expects an array
     * of strings not json objects.
     *
     */
    private static List<String> parseArray(JSONArray array){
        List<String> stringArray = new ArrayList<>();
        for(int i = 0, count = array.length(); i< count; i++)
        {
            try {
                String jsonObject = (String) array.get(i);
                stringArray.add(jsonObject);
            }
            catch (JSONException e) {
                Log.e("JsonRuleParser", e.getMessage());
            }
        }
        return stringArray;
    }

    /**** Concrete Condition Parsers ****/

    /**
     * Parsing a SimpleCondition consists in:
     *      (1) identifying the type of the SimpleCondtion;
     *      (2) check if this type is valid;
     *      (3) identifying the evaluator to be used by the SimpleCondition;
     *      (4) check if the evaluator is valid;
     *      (5) identifying the fixed_value to be passed to the SimpleCondition;
     *      (6) obtain a Map of the the key/value pairs contained in the fixed_value JSON object.
     *
     * @param conditionValue JSONObject which contains the necessary information for creating an instance of a SimpleCondition.
     * @return Instance of a SimpleCondition object.
     * */
    @Nullable
    private static SimpleConditionAbstract parseSimpleCondition(Context context, JSONObject conditionValue) throws JsonRuleException{

      SimpleConditionAbstract simpleCondition;
      IEvaluator evaluator;
      String conditionType;
      Map<String, Object> fixedValuePropertiesMap;

      try {
        conditionType =
            conditionValue.getString(JsonRuleContext.CONTEXT);

        String evaluatorType = conditionValue.getString(JsonRuleContext.EVALUATOR);
        evaluator = Evaluator.createEvaluator(evaluatorType);
        if (evaluator == null) {
          throw new JsonRuleException(
              "The evaluator specified for the SimpleCondition is not valid.");
        }

        JSONObject fixedValue = conditionValue.getJSONObject(JsonRuleContext.FIXED_VALUE);
        fixedValuePropertiesMap = parseFixedValueJsonObjectToMap(fixedValue);

      } catch (JSONException e) {
        throw new JsonRuleException("Baldy formed JSON Simple Condition object ");
      }

      simpleCondition = SimpleCondition.createSimpleCondition(
          context,
          conditionType,
          fixedValuePropertiesMap,
          evaluator);

      if (simpleCondition == null) {
        throw new JsonRuleException("The type specified for the SimpleCondition is not valid.");
      }
      return simpleCondition;
    }

    /**
     * Extracts the key/value pairs from the jsonObject.
     *
     * This Method only accepts JSONObjects which consist of String values.
     *
     * @param jsonObject JSON object from which to extract the Key/Value pairs.
     * @return Key/Value pairs, in the form of a Map<String,String> which where in the JSONObject.
     * @throws JsonRuleException when a properties value is not a String.
     * */
    private static Map<String,Object> parseFixedValueJsonObjectToMap(JSONObject jsonObject) throws JsonRuleException{
            Map<String, Object> map = new HashMap<>();

            Iterator<String> keyIt = jsonObject.keys();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                try {
                  // Place the key/value pair in the map.
                  map.put(key, jsonObject.get(key));
                }catch(JSONException e){
                    // If this Exception is caught, it is because one of the values which we tried
                    // to extract from the JSONObject was not a String.
                    throw new JsonRuleException("One of the properties of the fixed_value object was not a String.");
                }
            }

            return map;
    }

    /**
     * Parsing a ComposedCondition consists in:
     *      (1) identifying the relation specified between the condtions;
     *      (2) parsing the conditions which the ComposedCondition will consist of;
     *      (3) creating and returning the instance of ComposedCondition.
     * @param conditionValue JSONObject which contains the necessary information for creating an instance of a ComposedCondition.
     * @return Instance of a ComposedCondition object.
     * */
    @Nullable
    private static ComposedConditionAbstract parseComposedCondition(Context context, JSONObject conditionValue) throws JsonRuleException{
      ICondition one, two;
      ComposedConditionAbstract composedCondition;

      try {
        JSONObject condition1 = conditionValue.getJSONObject(JsonComposedConditionValueModel.COMPOSED_CONDITION_CONDITION_1);
        JSONObject condition2 = conditionValue.getJSONObject(JsonComposedConditionValueModel.COMPOSED_CONDITION_CONDITION_2);
        one = parseCondition(context, condition1);
        two = parseCondition(context, condition2);

        String relationOfComposedCond = conditionValue
            .getString(JsonComposedConditionValueModel.COMPOSED_CONDITION_RELATION);
       composedCondition =
            ComposedCondition.createComposedCondition(relationOfComposedCond, one, two);
        if (composedCondition == null) {
          throw new JsonRuleException(
              "The 'relation' specified for the ComposedCondition is not valid.");
        }
      }catch(JSONException e) {
        throw new JsonRuleException("Badly formed JSON Composed Condition object");
      }

      return composedCondition;
    }
}

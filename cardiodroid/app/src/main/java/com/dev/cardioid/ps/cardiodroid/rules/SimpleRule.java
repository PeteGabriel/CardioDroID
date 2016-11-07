package com.dev.cardioid.ps.cardiodroid.rules;

import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleContext;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonRuleModel;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 */

public class SimpleRule {

    public static final String TAG = Utils.makeLogTag(SimpleRule.class);

    private int id;
    private String name;
    private ICondition condition;
    private Collection<IAction> actions;
    private JSONObject nativeRule;

    public SimpleRule(ICondition condition, Collection<IAction> actions, JSONObject nativeRule) {
        this.id = -1;
        this.condition = condition;
        this.actions = (actions == null) ?  new ArrayList<IAction>() : actions;
        this.nativeRule = nativeRule;
        try {
            this.name = nativeRule.getString(JsonRuleModel.RULE_NAME);
        } catch (JSONException e) {
            Log.e(TAG, "Rule without a name field.");
        }
    }

    public String getName() {
        return name;
    }

    public Collection<IAction> getActions() {
        return actions;
    }

    public String getActionsDescription(){
        return "nada";
    }

    public String getContext(){
        try {
            return nativeRule.getJSONObject(JsonRuleContext.CONDITION)
                    .getJSONObject(JsonRuleContext.VALUE)
                    .getString(JsonRuleContext.CONTEXT);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            return "UNKNOWN";
        }
    }

    public String getOperator(){
        return "operator";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

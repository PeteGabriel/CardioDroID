package com.dev.cardioid.ps.cardiodroid.rules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.models.ContextLogInfo;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleContext;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleException;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRulesParser;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.JsonRuleModel;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A Rule consists in a condition (represented by the interface {@link ICondition}) and
 * a collection of actions (represented by the interface {@link IAction}).
 * If the condition of a rule is evaluated as true, all actions are called.
 */
public final class Rule implements Parcelable {

  private static final String TAG = Utils.makeLogTag(Rule.class);

  private int id;
  private String name;

  private ICondition condition;
  private Collection<IAction> actions;
  private JSONObject nativeRule;

  private List<String> contextDescription;

  public Rule(ICondition condition, Collection<IAction> actions, JSONObject nativeRule, List<String> contexts) {
    this.id = -1;
    this.condition = condition;
    this.contextDescription = contexts;
    this.actions = (actions == null) ?  new ArrayList<IAction>() : actions;
    this.nativeRule = nativeRule;
    try {
      this.name = nativeRule.getString(JsonRuleModel.RULE_NAME);
    } catch (JSONException e) {
      Log.e(TAG, "Rule without a name field.");
    }
  }

  public Rule(ICondition condition) {
    this.condition = condition;
    this.actions = new ArrayList<>();
    this.contextDescription = new ArrayList<>();
  }

  public static Rule buildFromJson(Context context, String rule) throws JsonRuleException{
    return JsonRulesParser.parseRule(context, rule);
  }

  public static Rule buildFromJson(Context context, JSONObject rule) throws JsonRuleException {
    return buildFromJson(context, rule.toString());
  }

  public void setID(int id) {
    this.id = id;
  }

  public int getID() {
    return id;
  }

  public JSONObject getNativeRule() {
    return nativeRule;
  }

  public String getName() {
    return name;
  }

  public ICondition getCondition() {
    return condition;
  }

  /**
   * Add an IAction to the collection of actions.
   */
  public void addAction(IAction action) {
    this.actions.add(action);
  }

  /**
   * Evaluates the ICondition and if this evaluation is true,
   * executes all of the actions.
   */
  public boolean evaluate() {
    return condition.evaluate();
  }

  /**
   * Auxiliary method which executes all of the actions.
   */
  public void doActions() {
    for (IAction a : actions)
      a.execute();
  }

  public String getTypeOfRule() {
    try {
      JSONObject tmp = getNativeRule().has(JsonRuleModel.RULE) ?
              getNativeRule().getJSONObject(JsonRuleModel.RULE) : getNativeRule();
      return tmp.getJSONObject(JsonRuleContext.CONDITION)
          .getString(JsonRuleContext.TYPE);
    } catch (JSONException e) {
      Log.e(TAG, "getTypeOfRule: " + e.getMessage());
      return "UNKNOWN";
    }
  }

  public String getActionsDescription(){
    String description = "";
    JSONArray actions;
    try {
      JSONObject tmp = getNativeRule().has(JsonRuleModel.RULE) ?
              getNativeRule().getJSONObject(JsonRuleModel.RULE) : getNativeRule();
      actions = tmp.getJSONArray(JsonRuleContext.ACTIONS);
      //description = TextUtils.join(", ", actions);
      int len = actions.length();
      for(int i = 0; i < len; ++i) {
        if (i > 0)
          description += ", ";
        description += actions.getString(i);
      }
      return description;
    } catch (JSONException e) {
      Log.e(TAG,  "getActionsDescription: " + e.getMessage());
      return "UNKNOWN";
    }
  }

  public List<String> getContextDescription() {
    return contextDescription;
  }

  public String getContext(){
    try {
      if (this.getTypeOfRule().equals(SimpleConditionAbstract.IDENTIFIER)){
        JSONObject tmp = getNativeRule().has(JsonRuleModel.RULE) ?
                getNativeRule().getJSONObject(JsonRuleModel.RULE) : getNativeRule();
        return tmp.getJSONObject(JsonRuleContext.CONDITION)
                .getJSONObject(JsonRuleContext.VALUE)
                .getString(JsonRuleContext.CONTEXT);
      }else{
        String oneKey = this.getContextNameOfSubconditionOne();
        String twoKey = this.getContextNameOfSubconditionTwo();
        String oneValue = this.getContextValueOfSubconditionOne();
        String twoValue = this.getContextValueOfSubconditionTwo();
        return String.format("%s: %s, %s: %s",
                oneKey, oneValue, twoKey, twoValue);
      }

    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
      return "UNKNOWN";
    }
  }


  public List<ContextLogInfo> getKeyValuesOfContexts(){
    List<ContextLogInfo> ctxList = new ArrayList<>();
    String tmp = this.getContext();
    final String SEPARATOR = " ";

    String[] allContexts = tmp.split(SEPARATOR);
    for (int i = 0; i < allContexts.length-1; i=+2) {
      ctxList.add(new ContextLogInfo(allContexts[i], allContexts[i+1]));
    }

    return ctxList;
  }

  public String getOperator() {
    String operator;
    try {
      JSONObject tmp = getNativeRule().has(JsonRuleModel.RULE) ?
              getNativeRule().getJSONObject(JsonRuleModel.RULE) : getNativeRule();

      operator = tmp.getJSONObject(JsonRuleModel.RULE_CONDITION)
          .getJSONObject(JsonRuleContext.VALUE)
          .getString(JsonRuleContext.EVALUATOR);
      return operator;
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
      return "UNKNOWN";
    }
  }

  @Override public String toString() {
    return "" + this.name + " ID: " + id;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.nativeRule.toString());
    dest.writeStringList(this.contextDescription);
  }

  protected Rule(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    try {
      this.nativeRule = new JSONObject(in.readString());
    } catch (JSONException e) {
      Log.e(TAG, "Bad parsing inside parcelable");
    }
    this.contextDescription = in.createStringArrayList();
  }

  public static final Creator<Rule> CREATOR = new Creator<Rule>() {
    @Override
    public Rule createFromParcel(Parcel source) {
      return new Rule(source);
    }

    @Override
    public Rule[] newArray(int size) {
      return new Rule[size];
    }
  };

  public String getTypeOfSubConditionOne() throws JSONException {
    return getSubCondition(JsonRuleContext.SUB_CONDITION_ONE)
            .getString(JsonRuleContext.TYPE);
  }

  public String getTypeOfSubConditionTwo() throws JSONException {
    return getSubCondition(JsonRuleContext.SUB_CONDITION_TWO)
            .getString(JsonRuleContext.TYPE);
  }

  public String getContextNameOfSubconditionOne() throws JSONException {
    return this.getSubCondition(JsonRuleContext.SUB_CONDITION_ONE)
            .getJSONObject(JsonRuleContext.VALUE)
            .getString(JsonRuleContext.CONTEXT);
  }

  public String getContextNameOfSubconditionTwo() throws JSONException {
    return this.getSubCondition(JsonRuleContext.SUB_CONDITION_TWO)
            .getJSONObject(JsonRuleContext.VALUE)
            .getString(JsonRuleContext.CONTEXT);
  }

  public String getContextValueOfSubconditionOne() throws JSONException {
    return this.getSubCondition(JsonRuleContext.SUB_CONDITION_ONE)
            .getJSONObject(JsonRuleContext.VALUE)
            .getString(JsonRuleContext.FIXED_VALUE);
  }

  public String getContextValueOfSubconditionTwo() throws JSONException {
    return this.getSubCondition(JsonRuleContext.SUB_CONDITION_TWO)
            .getJSONObject(JsonRuleContext.VALUE)
            .getString(JsonRuleContext.FIXED_VALUE);
  }


  private JSONObject getSubCondition(String conditionName) throws JSONException {
    return nativeRule.getJSONObject(JsonRuleContext.RULE_OBJECT)
        .getJSONObject(JsonRuleContext.CONDITION)
        .getJSONObject(JsonRuleContext.VALUE)
        .getJSONObject(conditionName);
  }
}

package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions;

import android.content.Context;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonExhaustionModel;
import java.util.Map;

public class ExhaustionSimpleCondition extends SimpleConditionAbstract<String, String> {

  private Context mContext;

  public ExhaustionSimpleCondition(Context ctx, Map<String, Object> fixedValueParams, IEvaluator eval) {
    super();
    mContext = ctx;
    this.fixedValue = (String)fixedValueParams.get(JsonExhaustionModel.EXHAUSTION_LEVEL);
    this.evaluator = eval;
  }

  @Override
  protected String getCurrentValue() {
    CardioDroidApplication app = (CardioDroidApplication) mContext.getApplicationContext();
    return app.getExhaustionStateValue() == null ? "UNKNOWN" : app.getExhaustionStateValue();
  }

  /**
   * The parameter will be the result of a call made to
   * getCurrentValue method.
   */
  @Override
  public boolean equalsFixed(String s) {
    return this.fixedValue.equals(s);
  }
}

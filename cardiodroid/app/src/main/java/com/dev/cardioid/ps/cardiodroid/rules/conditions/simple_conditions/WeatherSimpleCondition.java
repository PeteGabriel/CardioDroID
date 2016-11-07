package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions;

import android.content.Context;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonWeatherModel;

import java.util.Map;

public class WeatherSimpleCondition extends SimpleConditionAbstract<String, String> {

  private Context mContext;

  public WeatherSimpleCondition(Context ctx, Map<String, Object> fixedValueParams, IEvaluator eval) {
    super();
    mContext = ctx;
    this.fixedValue = (String) fixedValueParams.get(JsonWeatherModel.WEATHER_CONDITION_VALUE);
    this.evaluator = eval;
  }

  @Override protected String getCurrentValue() {
    CardioDroidApplication app = (CardioDroidApplication) mContext.getApplicationContext();
    return app.getCurrentWeather().getCurrentObservation().getWeather();
  }

  @Override public boolean equalsFixed(String s) {
    return this.fixedValue.equals(s);
  }


}

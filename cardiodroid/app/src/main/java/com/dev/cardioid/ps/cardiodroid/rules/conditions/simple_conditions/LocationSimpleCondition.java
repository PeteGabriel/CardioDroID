package com.dev.cardioid.ps.cardiodroid.rules.conditions.simple_conditions;

import android.content.Context;
import android.location.Location;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.models.CircularArea;
import com.dev.cardioid.ps.cardiodroid.rules.IEvaluator;
import com.dev.cardioid.ps.cardiodroid.rules.conditions.SimpleConditionAbstract;
import com.dev.cardioid.ps.cardiodroid.rules.parser.models.contexts.JsonLocationModel;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

public class LocationSimpleCondition extends SimpleConditionAbstract<Location, CircularArea> {

  private Context mContext;

  public LocationSimpleCondition(Context ctx, Map<String, Object> fixedValueParams, IEvaluator eval) {
    super();
    mContext = ctx;
    LatLng location = new LatLng(
        (double) fixedValueParams.get(JsonLocationModel.LATITUDE),
        (double) fixedValueParams.get(JsonLocationModel.LONGITUDE));

    this.fixedValue = new CircularArea(
            (String)fixedValueParams.get(JsonLocationModel.REQUEST_ID),
            location,
            (int)fixedValueParams.get(JsonLocationModel.RADIUS)
    );

    this.evaluator = eval;
  }


  @Override
  protected Location getCurrentValue() {
    CardioDroidApplication app = (CardioDroidApplication) mContext.getApplicationContext();
    return app.getCurrentLocation();
  }

  @Override public boolean equalsFixed(Location s) {
    return isOutOfRange(this.fixedValue.getLocation(), s, this.fixedValue.getRadius());
  }



  private boolean isOutOfRange(LatLng fixed, Location received, double radius){
    float[] distance = new float[2];

    Location.distanceBetween( fixed.latitude, fixed.longitude,
            received.getLatitude(), received.getLongitude(), distance);

    return distance[0] > radius;
  }

}

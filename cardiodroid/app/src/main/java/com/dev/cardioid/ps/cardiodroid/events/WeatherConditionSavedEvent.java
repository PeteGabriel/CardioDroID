package com.dev.cardioid.ps.cardiodroid.events;

import com.dev.cardioid.ps.cardiodroid.network.dtos.WeatherObservation;

/**
 * TODO
 */
public class WeatherConditionSavedEvent {

  private WeatherObservation bean;

  public WeatherConditionSavedEvent(WeatherObservation bean){
    this.bean = bean;
  }

  public WeatherObservation getWeatherCondition() {
    return bean;
  }
}

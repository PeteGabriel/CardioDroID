package com.dev.cardioid.ps.cardiodroid.events;

/**
 * TODO
 */
public class ExhaustionStateAvailableEvent {


  private String stateValue;

  public ExhaustionStateAvailableEvent(String value){
    stateValue = value;
  }

  public String getStateValue() {
    return stateValue;
  }


}

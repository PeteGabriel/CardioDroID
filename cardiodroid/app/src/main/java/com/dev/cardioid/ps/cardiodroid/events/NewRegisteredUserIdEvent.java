package com.dev.cardioid.ps.cardiodroid.events;

/**
 * TODO
 */
public class NewRegisteredUserIdEvent {

  private String userID;

  public NewRegisteredUserIdEvent(String id){
    this.userID = id;
  }

  public String getUserID() {
    return this.userID;
  }
}

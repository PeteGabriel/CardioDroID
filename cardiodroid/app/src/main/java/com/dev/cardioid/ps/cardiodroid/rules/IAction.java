package com.dev.cardioid.ps.cardiodroid.rules;


/**
 * Represents an action that is executable.
 * */
public interface IAction {
    /**
     * Executes the action.
     * */
    void execute();


  String[] getActionRelations = new String[]{
      Type.ALARM_SOUND_ACTION,
      Type.VIBRATE_ACTION
  };

  class Type{
    public static final String ALARM_SOUND_ACTION = "ALARM_SOUND";
    public static final String REMOTE_LOG_ACTION = "REMOTE_LOG";
    public static final String VIBRATE_ACTION = "VIBRATE";

  }
}

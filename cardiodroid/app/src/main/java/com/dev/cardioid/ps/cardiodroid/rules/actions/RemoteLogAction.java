package com.dev.cardioid.ps.cardiodroid.rules.actions;

import com.dev.cardioid.ps.cardiodroid.rules.IAction;

/**
 * Action that represents a remote upload of information
 * to the web API.
 */
public class RemoteLogAction implements IAction {

  @Override
  public void execute() {
    throw new UnsupportedOperationException();
  }

  @Override public String toString() {
    return "REMOTE_LOGGING ACTION";
  }
}

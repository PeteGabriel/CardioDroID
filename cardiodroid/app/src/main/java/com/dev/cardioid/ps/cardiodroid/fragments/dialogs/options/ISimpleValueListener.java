package com.dev.cardioid.ps.cardiodroid.fragments.dialogs.options;

import org.json.JSONObject;

/**
 * This event lets the hosting activity know when an element is
 * selected.
 */
public interface ISimpleValueListener {

  void onSimpleValueSelected(JSONObject value);
}

package com.dev.cardioid.ps.cardiodroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.activities.UserPreferencesActivity;

/**
 * Provides easy retrieval from preferences.
 *
 */
public class PreferencesUtils {

  public static final String TAG = Utils.makeLogTag(PreferencesUtils.class);

  public static final String PREFERENCES_READ_OP_DEFAULT_VALUE = "NOT_FOUND";


  public static final String AUTH_PROCESS = "TYPE_AUTH";
  public static final String ID_PROCESS = "TYPE_ID";


  public static final String LOCATION_LATITUDE = "LATITUDE";
  public static final String LOCATION_LONGITUDE = "LONGITUDE";

  /**
   * No instances of this class
   */
  private PreferencesUtils(){/*no instances*/}

  /**
   * TODO
   */
  public static boolean isUserRegistered(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean predicate = prefs.contains(UserPreferencesActivity.USER_ID_KEY);
    Log.d(TAG, "IsUserRegister: " + predicate);
    return predicate;
  }

  /**
   * TODO
   */
  public static String getRegisteredUserId(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String userID = prefs.getString(UserPreferencesActivity.USER_ID_KEY,
        PREFERENCES_READ_OP_DEFAULT_VALUE);

    Log.d(TAG, "RegisteredUserId: " + userID);
    return userID;
  }

  public static void saveUserRegisterId(Context context, String addr){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(UserPreferencesActivity.USER_ID_KEY, addr);
    editor.apply();
  }

  public static void saveDeviceAddress(Context context, String addr) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(UserPreferencesActivity.DEVICE_ADDR_KEY, addr);
    editor.apply();
  }

  public static boolean isDeviceAddressFound(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    boolean predicate = prefs.contains(UserPreferencesActivity.DEVICE_ADDR_KEY);
    Log.d(TAG, "isDeviceAddressFound: " + predicate);
    return predicate;
  }

  public static String getDeviceAddress(Context context){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String value =  prefs.getString(UserPreferencesActivity.DEVICE_ADDR_KEY, PREFERENCES_READ_OP_DEFAULT_VALUE);
    Log.d(TAG, "DeviceAddress: " + value);
    return value;
  }


  public static String getIdentificationProcess(Context ctx){
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String process = prefs.getString(UserPreferencesActivity.USER_IDENTIFICATION_PROCESS_KEY,
        PREFERENCES_READ_OP_DEFAULT_VALUE);
    Log.d(TAG, "Identification Process: " + process);
    return process;
  }


  /**
   * It tries to find the type of connection defined by the user in preferences.
   * It returns -1 if any type can be used.
   * If the WiFi was the value specified it will return the constant ConnectivityManager.TYPE_WIFI.
   * If the mobile data type was selected it will return ConnectivityManager.TYPE_MOBILE.
   */
  public static long getConnectionSpecifiedByUser(Context ctx){
    //possible values
    final String WIFI = ctx.getResources().getString(R.string.wifi_conn);
    final String MOBILE = ctx.getResources().getString(R.string.mobile_conn);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String valueFromPrefs = prefs.getString(UserPreferencesActivity.TYPE_OF_CONNECTION_KEY, "");

    if(valueFromPrefs.equals(WIFI))
      return ConnectivityManager.TYPE_WIFI;
    if(valueFromPrefs.equals(MOBILE))
      return ConnectivityManager.TYPE_MOBILE;
    else return -1;
  }

  public static int getTypeOfMapLayout(Context ctx){
    final String key = "types_of_map_list_preference";
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String valueFromPrefs = prefs.getString(key, "");
    return Integer.valueOf(valueFromPrefs);
  }

  public static int getVibrateActionDuration(Context ctx){
    final String key = "vibrate_time_key";
    final String DEFAULT_VIBRATE_TIME = "5000";
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String valueFromPrefs = prefs.getString(key, DEFAULT_VIBRATE_TIME);
    return Integer.valueOf(valueFromPrefs);
  }
}
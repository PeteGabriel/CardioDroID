package com.dev.cardioid.ps.cardiodroid.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Utility class that provides actions related to the
 * location context.
 */
public class LocationUtils {

  private LocationUtils(){

  }

  /**
   * Check if the device's location service is enabled.
   *
   * Return true if so, false otherwise.
   */
  public static boolean isLocationEnabled(Context context) {
    int locationMode = Settings.Secure.LOCATION_MODE_OFF;
    String locationProviders;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
      try {
        locationMode = Settings.Secure.getInt(
            context.getContentResolver(),
            Settings.Secure.LOCATION_MODE);
      } catch (Settings.SettingNotFoundException e) {
        Log.e("LocationUtils", e.getMessage());
      }
      return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }else{
      locationProviders = Settings.Secure.getString(
          context.getContentResolver(),
          Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
      return !TextUtils.isEmpty(locationProviders);
    }
  }

  /**
   * Verify that Play Services is available. Since the working parts live in another app
   * on the device, the Play Services library is not always guaranteed to be working.
   *
   * Play Services will be available if the error code returned is equal to ConnectionResult.SUCCESS.
   *
   * The value ConnectionResult.SUCCESS is documented at
   * {@link "https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability.html#public-methods"}
   */
  public static int areGoogleServicesAvailable(Context ctx){
    return  GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ctx);
  }

  /**
   *Convert a given speed in meters/second into kilometers/hour.
   *
   * @param speed speed in meters/second.
   * @return speed in Km/h.
   */
  public static float convertSpeedToMetric(float speed){
    return (speed * 3600)/1000;
  }
}

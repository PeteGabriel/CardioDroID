package com.dev.cardioid.ps.cardiodroid.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Utility class created to separate code that checks for permissions
 * from the activities of the application.
 *
 * Methods targeted as API version M must be used only if the device
 * is running that same API. Verify always before calling each of them.
 */
public class PermissionUtils {


  /**
   * Permissions required to access the device's location sensor.
   */
  public static final String[] PERMISSIONS_LOCATION = {
      Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
  };

  /**
   * Permissions required to read and write into the device's file system.
   */
  public static final String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  /**
   * Request code to use when asking for location permissions.
   */
  public static final int ACCESS_LOCATION_CODE = 1;


  /**
   * Request code to use when asking for storage permissions.
   */
  public static final int ACCESS_STORAGE_CODE = 0;

  private PermissionUtils() {
    //no instances of this
  }

  public static boolean isAbleToAccessLocation(Context ctx){
    return isCoarseLocationPermissionGranted(ctx) || isFineLocationPermissionGranted(ctx);
  }

  private static boolean isApiM(){
    return (Build.VERSION.SDK_INT == Build.VERSION_CODES.M);
  }

  @TargetApi(Build.VERSION_CODES.M)
  private static boolean isCoarseLocationPermissionGranted(Context ctx) {
    if (isApiM()) {
      return (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
          == PackageManager.PERMISSION_GRANTED);
    }
    return true;
  }

  @TargetApi(Build.VERSION_CODES.M)
  private static boolean isFineLocationPermissionGranted(Context ctx) {
    if (isApiM()) {
      return ctx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean isWriteToExternalStoragePermissionGranted(Context ctx) {
    if (isApiM()) {
      return ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  @TargetApi(Build.VERSION_CODES.M)
  public static boolean isReadToExternalStoragePermissionGranted(Context ctx) {
    if (isApiM()) {
      return ctx.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }return true;
  }


  public static AlertDialog.Builder getPermissionsInputDialog(Context ctx, String title, String msg) {
    final boolean NOT_CANCELABLE = false;
    return new AlertDialog.Builder(ctx).setCancelable(NOT_CANCELABLE)
        .setTitle(title)
        .setMessage(msg);
  }

  public static boolean isAbleToAccessStorage(Context applicationContext) {
    return isReadToExternalStoragePermissionGranted(applicationContext)
        && isWriteToExternalStoragePermissionGranted(applicationContext);
  }
}

package com.dev.cardioid.ps.cardiodroid.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.utils.PermissionUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * Initial splash screen.
 */
public class SplashActivity extends AppCompatActivity {

  /**
   * Permissions required to access the device's location sensor.
   */
  private static String[] PERMISSIONS_LOCATION = {
      Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
  };

  /**
   * Permissions required to read and write into the device's file system.
   */
  private static String[] PERMISSIONS_STORAGE = {
      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  /**
   * Request code to use when asking for location permissions.
   */
  private static final int ACCESS_LOCATION_CODE = 1;

  /**
   * Request code to use when asking for storage permissions.
   */
  private static final int ACCESS_STORAGE_CODE = 0;

  /**
   * Key to use when inserting data into an intent related to the location permission's answer.
   */
  public static final String EXTRA_ACCESS_LOCATION_PERMISSION_KEY =
      Utils.ROOT_PACKAGE + ".permissions.extra.access.location.key";

  /**
   * Key to use when inserting data into an intent related to the storage permission's answer.
   */
  public static final String EXTRA_STORAGE_PERMISSION_KEY =
      Utils.ROOT_PACKAGE + ".permissions.extra.access.location.key";

  /**
   * Debug purposes
   */
  private final String TAG = Utils.makeLogTag(SplashActivity.class);

  private int mCounter;


  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mCounter = 0;

    //runtime permissions are for Android 6+
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      moveForward();
    } else {
      requestPermission();
    }
  }

  /**
   * Beginning in Android 6.0 (API level 23), users grant permissions
   * to apps while the app is running, not when they install the app.
   *
   * This method asks the user for permissions like accessing the location sensor
   * or to be able to write and read from the file system.
   */
  private void requestPermission() {
    //ask for Coarse Location type permission
    final boolean ableToAccessLocation = PermissionUtils.isAbleToAccessLocation(this);
    if (!ableToAccessLocation) {
      String title = "This app needs some permissions.",
          msg = "Allow CardioDroID to access device's location sensors ?";
      getPermissionsInputDialog(title, msg, PERMISSIONS_LOCATION, ACCESS_LOCATION_CODE).show();
    }

    //ask for permissions to write and read to the file system
    final boolean ableToWriteAndRead =
        PermissionUtils.isWriteToExternalStoragePermissionGranted(this)
            && PermissionUtils.isReadToExternalStoragePermissionGranted(this);
    if (!ableToWriteAndRead) {
      String title = "This app needs some permissions.",
          msg = "Allow CardioDroID to read and write into the file system ?";

      getPermissionsInputDialog(title, msg, PERMISSIONS_STORAGE, ACCESS_STORAGE_CODE).show();
    }

    //if none were necessary, permissions were already granted
    //if so let us move on..
    if (ableToAccessLocation && ableToWriteAndRead) {
      moveForward();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    Log.d(TAG, "onRequestPermissionsResult");
    switch (requestCode) {

      case ACCESS_LOCATION_CODE:
        Log.i(TAG, "Received response for Location permission request.");

        final boolean accessLocationGranted =
            (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                grantResults[1] == PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "Access Location Permission Result -> " + accessLocationGranted);
        mCounter += 1;
        break;

      case ACCESS_STORAGE_CODE:
        Log.i(TAG, "Received response for Storage permission request.");

        final boolean accessStorageGranted = (grantResults.length == 2 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            (grantResults[1] == PackageManager.PERMISSION_GRANTED));
        Log.d(TAG, "Access Location Permission Result -> " + accessStorageGranted);
        mCounter += 1;
        break;
    }
    if (mCounter == 2){
      moveForward();
    }
  }

  /**
   * Given a title, message and an array of permissions, it asks the user to consider
   * giving the following permissions to this app.
   *
   * Call AlertDialog#show after receiving this instance.
   *
   * @param title title string to appear on the top of the fragment
   * @param msg main message tp display inside the fragment
   * @param permissions permissions to ask for
   * @return an instance of {@link AlertDialog.Builder}
   */
  private AlertDialog.Builder getPermissionsInputDialog(String title, String msg,
      final String[] permissions, final int permission_code) {

    final boolean NOT_CANCELABLE = false;

    return new AlertDialog.Builder(this).setCancelable(NOT_CANCELABLE)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            //only to satisfy the compiler
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              requestPermissions(permissions, permission_code);
            }
          }
        })
        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, permissions[0] + " and " + permissions[1] + " not granted.");
            mCounter += 1;
            if (mCounter == 2){
              moveForward();
            }
          }
        })
        .setTitle(title)
        .setMessage(msg);
  }

  /**
   * Navigate to the next activity with an intent pre-configured.
   *
   */
  private void moveForward() {
    //startActivity(new Intent(this, DashboardActivity.class));
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }
}

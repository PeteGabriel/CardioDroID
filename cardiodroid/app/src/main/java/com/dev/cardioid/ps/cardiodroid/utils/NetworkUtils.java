package com.dev.cardioid.ps.cardiodroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class that provides methods to verify the existence of connectivity.
 *
 */
public class NetworkUtils {

  private NetworkUtils(){
    /*No instances*/
  }

  /**
   * Get the network info
   * @param context
   *          The application context
   * @return
   *      An instance of NetworkInfo
   */
  public static NetworkInfo getNetworkInfo(Context context){
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo();
  }

  /**
   * Checks if exists an internet connectivity according with the user preferences.
   * @param ctx
   *      The application context
   * @return
   *      True if was found a valid connection.
   */
  public static boolean isConnected(Context ctx, long connType){
    if(connType == ConnectivityManager.TYPE_MOBILE)
      return isConnectedToMobileData(ctx);
    if(connType == ConnectivityManager.TYPE_WIFI)
      return isConnectedToWifi(ctx);
    return isConnected(ctx);
  }

  /**
   * Check for any connectivity
   * @param context
   *          The application context
   * @return
   *      True if is connected false otherwise.
   */
  public static boolean isConnected(Context context){
    NetworkInfo info = NetworkUtils.getNetworkInfo(context);
    return (info != null && info.isConnected());
  }


  /**
   * Check for connectivity to a Wifi network
   * @param context
   *          The application context
   * @return
   *      True if is connected to wifi.
   */
  public static boolean isConnectedToWifi(Context context){
    NetworkInfo info = NetworkUtils.getNetworkInfo(context);
    return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
  }

  /**
   * Check for any connectivity to a mobile network
   * @param context
   *          The application context
   * @return
   *      True if is connected to a mobile data connection.
   */
  public static boolean isConnectedToMobileData(Context context){
    NetworkInfo info = NetworkUtils.getNetworkInfo(context);
    return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
  }


  public final static class HttpCodes{
    public static final int CONFLICT = 409;
  }
}

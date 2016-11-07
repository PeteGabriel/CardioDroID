package com.dev.cardioid.ps.cardiodroid.utils;

import android.os.Build;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;

/**
 * Utility class that holds general helper methods.
 *
 */
public class Utils {

  /**
   * Use this method to create a string tag in an uniform way, every time.
   * @param cls the class interested in getting a tag
   * @return string tag
   */
  public static String makeLogTag(Class cls)
  {
    return "cardioid__" + cls.getSimpleName();
  }


  public static String ROOT_PACKAGE = "com.dev.cardioid.ps.cardiodroid";


  public static String bytesToString(byte[] bytes){
    StringBuilder stringBuilder = new StringBuilder(
        bytes.length);
    for (byte byteChar : bytes)
      stringBuilder.append(String.format("%02X ", byteChar));
    return convertHexToString(stringBuilder.toString().replaceAll(" ", ""));
  }

  //string nao pode conter espaços, por isso é que faço o replaceAll no método anterior
  private static String convertHexToString(String hex){
    StringBuilder sb = new StringBuilder();

    for( int i=0; i < hex.length()-1; i += 2 ){
      String output = hex.substring(i, (i + 2));
      int decimal = Integer.parseInt(output, 16);
      sb.append((char)decimal);
    }
    return sb.toString();
  }

  public static boolean isApiAbove23(){
    Log.d("BUILD", ""+Build.VERSION.SDK_INT);
    return Build.VERSION.SDK_INT >= 23;
  }


  /**
   * MUST BE INVOKED ON THE UI THREAD
   */
  public static void showInternetErrorToast(CardioDroidApplication mApp) {
    String msg = mApp.getResources().getString(R.string.connectivity_problem_explanation);
    ToastUtils.showError(msg, mApp);
  }


}
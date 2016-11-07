package com.dev.cardioid.ps.cardiodroid.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dev.cardioid.ps.cardiodroid.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Utility class created to remove boilerplate code related to the creation of
 * simple toasts. It also removes the need to remember to call the "show" method.
 *
 */
public class ToastUtils {

  private ToastUtils() {
    //no instances
  }

  /**
   * Displays a Toast message with a big length.
   *
   * @param message
   *  The message to be displayed
   * @param context
   *      The application's context
   */
  public static void showError(final String message, final Context context) {
    getToast(message, context).show();
  }

  /**
   * Displays a Toast message with a small length.
   *
   * @param message
   *  The message to be displayed
   * @param context
   *      The application's context
   */
  public static void showShortMessage(String message, Context context) {
    getToast(message, context, Toast.LENGTH_SHORT).show();
  }

  public static void showMessage(String msg, Context context){
    getToast(msg, context).show();
  }

  private static Toast getToast(String message, Context context) {
    return getToast(message, context, Toast.LENGTH_LONG);
  }

  private static Toast getToast(String message, Context context, int length) {
    return Toast.makeText(context, message, length);
  }


  public static void showNotification(Context context, String title, String contentText){
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(contentText);
    // Sets an ID for the notification
    int mNotificationId = 1;
    // Gets an instance of the NotificationManager service
    NotificationManager mNotifyMgr =
            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    // Builds the notification and issues it.
    mNotifyMgr.notify(mNotificationId, mBuilder.build());
  }
}
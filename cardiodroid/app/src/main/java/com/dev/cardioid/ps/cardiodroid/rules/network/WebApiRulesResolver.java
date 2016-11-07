package com.dev.cardioid.ps.cardiodroid.rules.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling downloads and uploads of
 * rules from and to the web API.
 */
public class WebApiRulesResolver extends IntentService {

  /**
   * Action that should be used to indicate that the service
   * must download a file of rules.
   *
   * TODO decidir como estes ficheiros irão ser escolhidos
   */
  private static final String ACTION_DOWNLOAD = "";

  /**
   * Action that should be used to indicate that the service
   * must upload a set of rules.
   */
  private static final String ACTION_UPLOAD = "";


  // TODO: Add/Rename parameters

  /**
   * Ctor
   */
  public WebApiRulesResolver() {
    super("WebApiRulesResolver");
  }

  /**
   * Starts this service to perform action Download.
   * If the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionDownload(Context context) {
    Intent intent = new Intent(context, WebApiRulesResolver.class);
    intent.setAction(ACTION_DOWNLOAD);
    context.startService(intent);
  }

  /**
   * Starts this service to perform action Upload.
   *
   * @see IntentService
   */
  public static void startActionUpload(Context context) {
    Intent intent = new Intent(context, WebApiRulesResolver.class);
    intent.setAction(ACTION_UPLOAD);
    context.startService(intent);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_DOWNLOAD.equals(action)) {
        handleActionDownload();
      } else if (ACTION_UPLOAD.equals(action)) {
        handleActionUpload();
      }
    }
  }

  /**
   * Handle action Download in the provided background thread.
   */
  private void handleActionDownload() {
    // TODO: Handle action Download
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * Handle action Upload in the provided background thread.
   */
  private void handleActionUpload() {
    // TODO: Handle action Upload
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

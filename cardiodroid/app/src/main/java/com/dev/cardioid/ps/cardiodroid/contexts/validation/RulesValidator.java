package com.dev.cardioid.ps.cardiodroid.contexts.validation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.R;
import com.dev.cardioid.ps.cardiodroid.models.LogInfo;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.repo.RepoUtils;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.utils.ToastUtils;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

import java.util.List;

/**
 * This class performs the validation of each rule present inside the
 * device.
 * It expects the type of context that initiated this verification
 * in order to search for rules with that context inside. If any is found
 * the evaluation process will be initiated.
 *
 */
public class RulesValidator extends IntentService {

  public static final String TAG = Utils.makeLogTag(RulesValidator.class);

  /*
   * Action this service can perform.
   */
  private static final String VALIDATE_RULES =
      "com.dev.cardioid.ps.cardiodroid.contexts.validation.action.validate.rules";

  /*
   * The key to send and retrieve data later on.
   */
  private static final String EXTRA_CONTEXT_TYPE =
      "com.dev.cardioid.ps.cardiodroid.contexts.validation.extra.context.type";

  private static final String EXTRA_VALUE =
      "com.dev.cardioid.ps.cardiodroid.contexts.validation.extra.value";

  /**
   * ctor
   */
  public RulesValidator() {
    super("RulesValidator");
  }


  /**
   * Starts this service to perform the action with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startService(Context context, String param1) {
    Intent intent = new Intent(context, RulesValidator.class);
    intent.setAction(VALIDATE_RULES);
    intent.putExtra(EXTRA_CONTEXT_TYPE, param1);
    context.startService(intent);
  }

  public static void startService(Context context, String param1, String param2) {
    Intent intent = new Intent(context, RulesValidator.class);
    intent.setAction(VALIDATE_RULES);
    intent.putExtra(EXTRA_CONTEXT_TYPE, param1);
    intent.putExtra(EXTRA_VALUE, param2);
    context.startService(intent);
  }


  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (VALIDATE_RULES.equals(action)) {
        final String param1 = intent.getStringExtra(EXTRA_CONTEXT_TYPE);
        final String param2 = intent.getStringExtra(EXTRA_VALUE);
        handleAction(param1, param2);
      }
    }
  }

  /**
   * Main handler. Given a certain context type, it will search for rules
   * with the same context.
   */
  private void handleAction(final String contextTypeInitiator, final String extraValue) {
    final CardioDroidApplication appInstance = (CardioDroidApplication)getApplicationContext();
    String activeEmail = appInstance.getPersonalUserAccountInfo().getEmail();
    Uri rulesForActiveUser = Uri.withAppendedPath(RepoUtils.makeUriForTableRules(), activeEmail);
    appInstance.getRepository().getRules(rulesForActiveUser, new Completion<List<Rule>>() {
      @Override public void onResult(CallResult<List<Rule>> result) {
        List<Rule> rules;
        try {
          rules = result.getResult();
        } catch (Exception e) {
          Log.e(TAG, "Could not obtain a list of Rules");
          return;
        }
        for (Rule singleRule : rules){
          List<String> contextsOfRule = singleRule.getContextDescription();
          for (String context : contextsOfRule){
            if (context.equals(contextTypeInitiator)){
              Log.d(TAG, singleRule.getName() + " has the context type of " + contextTypeInitiator);
              if (singleRule.evaluate()){
                Log.d(TAG, singleRule.getName() + "is now valid. Prepare to ACT !!! ");


                //show a small notification. X and Y are placeholders inside the string
                String content = getResources().getString(R.string.cardiodroid_notification_content);
                content = content.replace("X", singleRule.getName());
                content = content.replace("Y", singleRule.getContext());
                String title = getResources().getString(R.string.cardiodroid_notification_title);
                ToastUtils.showNotification(getApplicationContext(), title, content);

                //perform actions
                singleRule.doActions();

                //send info to the web service
                LogInfo logInfo = new LogInfo(appInstance.getPersonalUserAccountInfo().getEmail());
                logInfo.setContexts(singleRule.getKeyValuesOfContexts());
                appInstance.getCardioApiProvider().logInfo(logInfo, new Completion<Void>() {
                  @Override
                  public void onResult(CallResult<Void> result) {
                    try{
                      result.getResult();
                      Log.d(TAG, "Information logged successfully");
                    }catch(Exception e){
                      Log.e(TAG, e.getMessage());
                    }
                  }
                });
              }
            }
          }
        }
      }
    });
  }

}

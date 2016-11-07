package com.dev.cardioid.ps.cardiodroid.repo;

import android.app.Application;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.CardioDroidApplication;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.List;

/**
 * The concrete implementation of a local repository that follows the guidelines
 * specified by the interface {@link IRepository}.
 */
public class Repository implements IRepository{


  public static final String TAG = Utils.makeLogTag(Repository.class);

  /**
   * The reference for the implementation of AsyncQueryHandler
   */
  private RepoQueryHandler mQueryHandler;

  private CardioDroidApplication mApp;

  public Repository(Application app){
    mApp = (CardioDroidApplication) app;
    mQueryHandler = new RepoQueryHandler(mApp.getContentResolver(), mApp);
  }


  @Override
  public void getRules(final Uri resourceUri, Completion<List<Rule>> onResult) {
    Log.d(TAG, "Getting all rules from repo");

    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();

    mQueryHandler.startQuery(RepoQueryHandler.HANDLER_QUERY,
        onResult,
        resourceUri,
        new String[]{ DataUnit.RULE_ID, DataUnit.RULE_BODY },
        DataUnit.RULE_USER_EMAIL + "=?",
        new String[]{ activeEmail },
        null);
  }

  @Override
  public void insertRule(String jsonRule, final Completion<Uri> onInsertCompleteHandler) {
    Log.d(TAG, "Insert");
    Uri resourceUri = RepoUtils.makeUriForTableRules();
    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();
    resourceUri = Uri.withAppendedPath(resourceUri, activeEmail);

    Log.d(TAG, "Trying to insert resource with URI: " + resourceUri);
    ContentValues values = new ContentValues();
    values.put(DataUnit.RULE_USER_EMAIL, activeEmail);
    values.put(DataUnit.RULE_BODY, jsonRule);

    mQueryHandler.startInsert(RepoQueryHandler.HANDLER_INSERT, onInsertCompleteHandler, resourceUri, values);
  }

  @Override
  public void deleteRule(Uri resourceUri, Rule record, Completion<Void> cb) {
    Log.d(TAG, "Delete: " + resourceUri);

    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();

    mQueryHandler.startDelete(RepoQueryHandler.HANDLER_DELETE,
            cb,
            resourceUri,
            DataUnit.RULE_ID + "=? AND " + DataUnit.RULE_USER_EMAIL + "=?",
            new String[]{ String.valueOf(record.getID()), activeEmail }
    );
  }

  @Override
  public void updateRule(final Rule infoToUpdate, Completion<Void> cb) {

    final long id = infoToUpdate.getID();

    ContentValues values = new ContentValues();
    values.put(DataUnit.RULE_BODY, infoToUpdate.getNativeRule().toString());
    values.put(DataUnit.RULE_ID, id);

    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();
    Uri pointerToResource = Uri.withAppendedPath(RepoUtils.makeUriForTableRules(), activeEmail);
    pointerToResource = Uri.withAppendedPath(pointerToResource, String.valueOf(id));

    mQueryHandler.startUpdate(RepoQueryHandler.HANDLER_UPDATE,
            cb,
            pointerToResource,
            values,
            DataUnit.RULE_ID + "=? AND " + DataUnit.RULE_USER_EMAIL + "=?",
            new String[]{String.valueOf(id), activeEmail});
  }

  @Override
  public void getRuleByID(Uri resourceUri, long idOfRule, Completion callback) {

    String activeEmail = mApp.getPersonalUserAccountInfo().getEmail();
    Log.d(TAG, "GetByID: " + resourceUri + " With Email \"" + activeEmail + "\"");

    //NOTE: This query must not contain any ID in its uri. The where clause will handle that.
    mQueryHandler.startQuery(RepoQueryHandler.HANDLER_QUERY,
        callback,
        resourceUri,
        new String[]{
            DataUnit.RULE_ID, DataUnit.RULE_BODY
        },
        DataUnit.RULE_ID + "=? AND " + DataUnit.RULE_USER_EMAIL + "=?",
        new String[]{String.valueOf(idOfRule), activeEmail},
        null);
  }
}

package com.dev.cardioid.ps.cardiodroid.repo;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.network.async.process.CallResult;
import com.dev.cardioid.ps.cardiodroid.network.async.process.Completion;
import com.dev.cardioid.ps.cardiodroid.rules.Rule;
import com.dev.cardioid.ps.cardiodroid.rules.parser.JsonRuleException;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of AsyncQueryHandler.
 * {@link android.content.AsyncQueryHandler} helps transforming a synchronous interface
 * into an asynchronous interface.
 *
 * When we call the methods from the ContentResolver class,
 * they are not asynchronous, although every access to the database should be async since we do not
 * know how long the result will take to be available.
 *
 * Using AsyncTask for this procedure is deviating from what they are actually supposed
 * to be used for. Using a AsyncQueryHandler is one solution and a better one.
 *
 * {@see AsyncQueryHandler}
 */
public class RepoQueryHandler extends AsyncQueryHandler{

  private static final String TAG = Utils.makeLogTag(RepoQueryHandler.class);

  //Tokens to be used while performing actions using the handler
  /**
   * When performing queries send this as token.
   */
  static final int HANDLER_QUERY = 1;
  static final int HANDLER_INSERT = 2;
  static final int HANDLER_DELETE = 0;
  static final int HANDLER_UPDATE = 3;

  private Context mContext;
  /**
   * Ctor
   *
   * @param cr
   *  an instance of the {@link ContentResolver} used by this handler.
   */
  public RepoQueryHandler(ContentResolver cr, Context context) {
    super(cr);
    mContext = context;
  }

  @Override
  protected void onInsertComplete(int token, Object cookie, Uri uri) {
    super.onInsertComplete(token, cookie, uri);
    if (uri != null) {
      Log.d(TAG, "Inserted new resource referenced by: " + uri.toString());
      if(cookie != null){
        Completion<Uri> callback = (Completion<Uri>)cookie;
        callback.onResult(new CallResult<>(uri));
      }
    }
  }

  @Override protected void onDeleteComplete(int token, Object cookie, int result) {
    Log.d(TAG, "Records deleted: " + result);
    if(cookie != null){
      Completion callback = (Completion)cookie;
      callback.onResult(null);
    }
    Log.d(TAG, "Deleted rows: " + result);
  }

  @Override protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
    super.onQueryComplete(token, cookie, cursor);
    if (cookie != null){
      getListOfRules(cursor, (Completion<List<Rule>>) cookie);
    }
  }

  @Override
  protected void onUpdateComplete(int token, Object cookie, int result) {
    super.onUpdateComplete(token, cookie, result);
    Log.d(TAG, "Updated rows: " + result);
    if(cookie != null){
      Completion callback = (Completion)cookie;
      callback.onResult(null);
    }
  }

  private void getListOfRules(Cursor cursor, Completion<List<Rule>> onResult) {
    List<Rule> records = new ArrayList<>();

    if (cursor == null) {
      onResult.onResult(new CallResult<List<Rule>>(new Exception("Empty List of Rules")));
      return;
    }

    if(cursor.moveToFirst()) {
      while (!cursor.isAfterLast()) {
        try {
          Rule record = Rule.buildFromJson(mContext, cursor.getString(1)); //create based upon the body
          record.setID(cursor.getInt(0));
          Log.d(TAG, record.getName() + " retrieved ! ");
          records.add(record);
        }catch (JsonRuleException e){
          Log.e(TAG, e.getMessage());
        }
        cursor.moveToNext();
      }
      cursor.close();
      onResult.onResult(new CallResult<>(records));
    }else{
      cursor.close();
      onResult.onResult(new CallResult<List<Rule>>(new Exception("Error retrieving the result")));
    }
  }
}

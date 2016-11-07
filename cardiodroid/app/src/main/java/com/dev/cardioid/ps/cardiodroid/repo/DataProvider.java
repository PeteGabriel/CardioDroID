package com.dev.cardioid.ps.cardiodroid.repo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;

/**
 * The concrete implementation of a ContentProvider.
 * This class performs actions that act over the data inside the database.
 *
 * This class specifies the authority and the uris accepted by this provider. Given this, other
 * applications can access the data inside this application's database.
 *
 * @version 1.0.0
 */
public class DataProvider extends ContentProvider {

  private static final String TAG = Utils.makeLogTag(DataProvider.class);

  /**
   * A reference to UriMatcher to aid in matching URIs in content providers
   */
  private static final UriMatcher sURIMatcher;

  /**
   * The authority of this provider
   */
  public static final String AUTHORITY = "com.dev.cardioid.ps.cardiodroid";

  /**
   * The basic uri of this provider
   */
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  /**
   * These constants mean the codes used for each uri
   * when matching against the UriMatcher.
   */
  public static final int RULES_TABLE_CODE = 1;
  public static final int RULES_TABLE_WITH_INDEX_CODE = 2;

  //To use with the matcher
  private static final String RULES_TABLE_NAME_MATCHER = "/rules/*";
  private static final String RULES_TABLE_NAME_INDEX_APPENDED_MATCHER = "/rules/*/#";

  //To expose outside of this class
  public static final String RULES_TABLE_NAME = "/rules/";


  //This procedure is advised by Google when dealing with ContentProviders
  static {
    sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sURIMatcher.addURI(AUTHORITY, RULES_TABLE_NAME_MATCHER, RULES_TABLE_CODE);
    sURIMatcher.addURI(AUTHORITY, RULES_TABLE_NAME_INDEX_APPENDED_MATCHER, RULES_TABLE_WITH_INDEX_CODE);
  }

  /**
   * An instance of the database
   */
  private DatabaseHelper db;

  public DataProvider() {
  }

  @Override
  public boolean onCreate() {
    db = new DatabaseHelper(getContext().getApplicationContext());
    return true;
  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    //code that figures out the table to delete from
    String table = getTableForUri(uri);

    int count = db.getWritableDatabase().delete(table, selection, selectionArgs);

    getContext().getContentResolver().notifyChange(uri, null);

    return (count);
  }

  @Override public String getType(Uri uri) {
    // TODO: Implement this to handle requests for the MIME type of the data
    // at the given URI.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override public Uri insert(Uri uri, ContentValues values) {
    String table = getTableForUri(uri);
    Log.d(TAG, "INSERT Resource with uri: " + uri + " for table " + table);

    try{
      long rowID = db.getWritableDatabase().insertOrThrow(table, null, values);

      if (rowID > 0) {
        Uri uriToNewResource = ContentUris.withAppendedId(uri, rowID);
        getContext().getContentResolver().notifyChange(uri, null);
        return(uriToNewResource);
      }
    }catch(SQLException ex) {
      Log.e(TAG, ex.getMessage());
      return null;
    }
    return null;
  }


  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    String table = getTableForUri(uri);
    Cursor cursor = db.getReadableDatabase()
        .query(table, projection, selection, selectionArgs, null, null, null);
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    return cursor;
  }

  @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    String table = getTableForUri(uri);

    int count = db.getWritableDatabase().update(table, values, selection, selectionArgs);
    getContext().getContentResolver().notifyChange(uri, null);

    Log.d(TAG, "Total Updated Records: "+count);
    return count;
  }

  /**
   * For a given uri, get the correspondent database table.
   */
  private String getTableForUri(Uri uri) {
    int i = sURIMatcher.match(uri);
    switch (i) {
      case RULES_TABLE_CODE:
      case RULES_TABLE_WITH_INDEX_CODE:
        return DataUnit.Tables.RULES_TABLE;

      default:
        return "";
    }
  }

}

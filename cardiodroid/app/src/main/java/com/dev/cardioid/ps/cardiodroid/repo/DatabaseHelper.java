package com.dev.cardioid.ps.cardiodroid.repo;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.dev.cardioid.ps.cardiodroid.utils.Utils;
import android.database.sqlite.SQLiteDatabase;

/**
 * The concrete implementation of {@link SQLiteOpenHelper}
 * that provides a solution to the creation and update of a database
 * inside a device.
 *
 * Note that the class {@link SQLiteOpenHelper} used in this application is the
 * one distributed by the SQLite download page and not the one inside
 * the device where this app might run.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  public static final String TAG = Utils.makeLogTag(DatabaseHelper.class);

  /**
   * The name of the database.
   */
  private static final String DATABASE_NAME = "CARDROID_DB.db";

  /**
   * The version of the database.
   */
  private static final int DATABASE_VERSION = 1;

  /**
   * ctor
   */
  public DatabaseHelper(Context ctx) {
    super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase  sqLiteDatabase) {
    Log.d(TAG, "OnCreate called");

    //init tables
    sqLiteDatabase.execSQL(DataUnit.CREATE_RULES_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    Log.d(TAG, "OnUpgrade called");

    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS  " + DataUnit.Tables.RULES_TABLE);
    sqLiteDatabase.execSQL(DataUnit.CREATE_RULES_TABLE);
  }
}
